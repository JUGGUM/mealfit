package dev.mealfit.mealfit.unit.service

import dev.mealfit.mealfit.common.error.exception.InvalidLoginTypeException
import dev.mealfit.mealfit.user.application.login.LoginService
import dev.mealfit.mealfit.user.application.login.LoginStrategy
import dev.mealfit.mealfit.user.application.login.LoginStrategyFactory
import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock
    private lateinit var loginStrategyFactory: LoginStrategyFactory

    private lateinit var loginService: LoginService

    @BeforeEach
    fun setUp() {
        loginService = LoginService(loginStrategyFactory)
    }

    @Test
    @DisplayName("'local' 타입으로 로그인 시 로컬 전략이 실행되고 결과를 반환한다")
    fun `local 로그인 전략 정상 실행`() {
        val request = LoginRequest("test@example.com", "password123")
        val mockStrategy: LoginStrategy = mock()
        val expected = LoginResult("LOCAL", true, "user1", "jwt-token", "testuser")

        whenever(loginStrategyFactory.getStrategy("local")).thenReturn(mockStrategy)
        whenever(mockStrategy.login(request)).thenReturn(expected)

        val result = loginService.login("local", request)

        assertThat(result.loginType).isEqualTo("LOCAL")
        assertThat(result.success).isTrue()
        assertThat(result.token).isEqualTo("jwt-token")
        verify(mockStrategy, times(1)).login(request)
    }

    @Test
    @DisplayName("'kakao' 타입으로 로그인 시 카카오 전략이 실행된다")
    fun `kakao 로그인 전략 위임 검증`() {
        val request = LoginRequest("kakao-token", "")
        val kakaoStrategy: LoginStrategy = mock()
        val expected = LoginResult("KAKAO", true, "kakao123", "kakao-jwt", "kakaoUser")

        whenever(loginStrategyFactory.getStrategy("kakao")).thenReturn(kakaoStrategy)
        whenever(kakaoStrategy.login(request)).thenReturn(expected)

        val result = loginService.login("kakao", request)

        assertThat(result.loginType).isEqualTo("KAKAO")
        verify(kakaoStrategy, times(1)).login(request)
    }

    @Test
    @DisplayName("'naver' 타입으로 로그인 시 네이버 전략이 실행된다")
    fun `naver 로그인 전략 위임 검증`() {
        val request = LoginRequest("naver-token", "")
        val naverStrategy: LoginStrategy = mock()
        val expected = LoginResult("NAVER", true, "naver456", "naver-jwt", "naverUser")

        whenever(loginStrategyFactory.getStrategy("naver")).thenReturn(naverStrategy)
        whenever(naverStrategy.login(request)).thenReturn(expected)

        val result = loginService.login("naver", request)

        assertThat(result.loginType).isEqualTo("NAVER")
        verify(naverStrategy, times(1)).login(request)
    }

    @Test
    @DisplayName("지원하지 않는 로그인 타입 요청 시 InvalidLoginTypeException이 발생한다")
    fun `지원하지 않는 로그인 타입 예외 발생`() {
        val request = LoginRequest("test@example.com", "password123")

        whenever(loginStrategyFactory.getStrategy("github"))
            .thenThrow(IllegalArgumentException("Unsupported login type: github"))

        assertThatThrownBy { loginService.login("github", request) }
            .isInstanceOf(InvalidLoginTypeException::class.java)
    }

    @Test
    @DisplayName("빈 문자열 로그인 타입 요청 시 InvalidLoginTypeException이 발생한다")
    fun `빈 문자열 로그인 타입 예외 발생`() {
        val request = LoginRequest("test@example.com", "password123")

        whenever(loginStrategyFactory.getStrategy(""))
            .thenThrow(IllegalArgumentException("Unsupported login type: "))

        assertThatThrownBy { loginService.login("", request) }
            .isInstanceOf(InvalidLoginTypeException::class.java)
    }

    @Test
    @DisplayName("전략 factory 조회 실패 시 loginStrategy.login()은 호출되지 않는다")
    fun `factory 실패 시 strategy login 미호출`() {
        val request = LoginRequest("test@example.com", "pass")
        val mockStrategy: LoginStrategy = mock()

        whenever(loginStrategyFactory.getStrategy("invalid"))
            .thenThrow(IllegalArgumentException("Unsupported"))

        try { loginService.login("invalid", request) } catch (_: Exception) {}

        verify(mockStrategy, never()).login(any())
    }
}
