package dev.mealfit.mealfit.integration

import com.fasterxml.jackson.databind.ObjectMapper
import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.application.signup.SignUpService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var signUpService: SignUpService

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var dietSurveyService: DietSurveyService

    @BeforeEach
    fun setUp() {
        signUpService.signUp(SignUpRequest("testuser", "password123", "auth@example.com"))
    }

    @Test
    @DisplayName("POST /api/auth/login - 유효한 이메일/비밀번호로 로그인 시 200과 JWT 토큰을 반환한다")
    fun `로그인 성공 - JWT 토큰 반환`() {
        val request = LoginRequest("auth@example.com", "password123")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.token").isNotEmpty)
    }

    @Test
    @DisplayName("POST /api/auth/login - 잘못된 비밀번호로 로그인 시 401 Unauthorized를 반환한다")
    fun `잘못된 비밀번호 로그인 - 401 반환`() {
        val request = LoginRequest("auth@example.com", "wrongPassword!")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("POST /api/auth/login - 존재하지 않는 이메일로 로그인 시 401 Unauthorized를 반환한다")
    fun `존재하지 않는 이메일 로그인 - 401 반환`() {
        val request = LoginRequest("nonexistent@example.com", "password123")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("POST /api/auth/login - 로그인 성공 응답에 loginType 필드가 포함된다")
    fun `로그인 성공 응답 구조 검증`() {
        val request = LoginRequest("auth@example.com", "password123")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.loginType").exists())
            .andExpect(jsonPath("$.token").isNotEmpty)
    }

    @Test
    @DisplayName("POST /api/auth/signup - 정상 회원가입 요청이 200과 사용자 정보를 반환한다")
    fun `회원가입 API 정상 응답`() {
        val request = SignUpRequest("newuser", "pass1234", "new@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("new@example.com"))
            .andExpect(jsonPath("$.username").value("newuser"))
    }

    @Test
    @DisplayName("POST /api/auth/signup - 중복 이메일로 가입 시 400을 반환한다")
    fun `중복 이메일 가입 - 400 반환`() {
        val request = SignUpRequest("anotheruser", "pass1234", "auth@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("JWT 토큰 없이 보호된 엔드포인트 접근 시 401 Unauthorized를 반환한다")
    fun `JWT 없이 보호 엔드포인트 접근 - 401 반환`() {
        mockMvc.perform(get("/api/user/list"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("로그인으로 받은 JWT 토큰으로 보호된 엔드포인트에 접근할 수 있다")
    fun `JWT 토큰으로 보호 엔드포인트 접근 성공`() {
        val loginRequest = LoginRequest("auth@example.com", "password123")

        val loginResult = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseBody = objectMapper.readTree(loginResult.response.contentAsString)
        val token = responseBody["token"].asText()

        mockMvc.perform(
            get("/api/user/list")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
    }
}
