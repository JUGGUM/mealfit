package dev.mealfit.mealfit.integration

import com.fasterxml.jackson.databind.ObjectMapper
import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("SignUp 통합 테스트")
class SignUpIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    // 비동기 이벤트 핸들러가 트랜잭션 롤백 중 실행되어 충돌하는 것을 방지
    @MockitoBean
    private lateinit var dietSurveyService: DietSurveyService

    @Test
    @DisplayName("POST /api/auth/signup - 정상 회원가입 요청이 200과 사용자 정보를 반환한다")
    fun `회원가입 API 정상 응답`() {
        val request = SignUpRequest("testuser", "password123", "test@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.id").isNumber)
    }

    @Test
    @DisplayName("POST /api/auth/signup - 중복 이메일로 회원가입 시 400 Bad Request를 반환한다")
    fun `중복 이메일 회원가입 API 400 반환`() {
        val request = SignUpRequest("testuser", "password123", "dup@example.com")

        // 첫 번째 가입
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        // 동일 이메일 재가입 시도
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/auth/signup - 회원가입 후 DB에 사용자가 저장된다")
    fun `회원가입 후 DB에 사용자 저장 확인`() {
        val request = SignUpRequest("dbuser", "password123", "dbtest@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        assert(userRepository.existsByEmail("dbtest@example.com"))
    }

    @Test
    @DisplayName("POST /api/auth/signup - 응답 body에 비밀번호 필드가 노출되지 않는다")
    fun `회원가입 응답에 비밀번호 미노출`() {
        val request = SignUpRequest("secuser", "mySecret123", "sec@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.password").doesNotExist())
    }

    @Test
    @DisplayName("POST /api/auth/signup - 서로 다른 이메일로 두 번 가입 시 모두 성공한다")
    fun `서로 다른 이메일 두 번 가입 성공`() {
        val request1 = SignUpRequest("user1", "password123", "user1@example.com")
        val request2 = SignUpRequest("user2", "password123", "user2@example.com")

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        ).andExpect(status().isOk)

        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        ).andExpect(status().isOk)
    }
}
