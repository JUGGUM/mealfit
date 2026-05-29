package dev.mealfit.mealfit.unit.login

import dev.mealfit.mealfit.user.application.login.KakaoLoginStrategy
import dev.mealfit.mealfit.user.application.login.LocalLoginStrategy
import dev.mealfit.mealfit.user.application.login.LoginStrategyFactory
import dev.mealfit.mealfit.user.application.login.NaverLoginStrategy
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("LoginStrategyFactory 단위 테스트")
class LoginStrategyFactoryTest {

    private lateinit var factory: LoginStrategyFactory

    @BeforeEach
    fun setUp() {
        factory = LoginStrategyFactory(
            listOf(LocalLoginStrategy(), KakaoLoginStrategy(), NaverLoginStrategy())
        )
    }

    @Test
    @DisplayName("'local' 타입으로 조회하면 LocalLoginStrategy를 반환한다")
    fun `local 전략 반환 검증`() {
        val strategy = factory.getStrategy("local")

        assertThat(strategy).isInstanceOf(LocalLoginStrategy::class.java)
    }

    @Test
    @DisplayName("'kakao' 타입으로 조회하면 KakaoLoginStrategy를 반환한다")
    fun `kakao 전략 반환 검증`() {
        val strategy = factory.getStrategy("kakao")

        assertThat(strategy).isInstanceOf(KakaoLoginStrategy::class.java)
    }

    @Test
    @DisplayName("'naver' 타입으로 조회하면 NaverLoginStrategy를 반환한다")
    fun `naver 전략 반환 검증`() {
        val strategy = factory.getStrategy("naver")

        assertThat(strategy).isInstanceOf(NaverLoginStrategy::class.java)
    }

    @Test
    @DisplayName("대문자 'LOCAL' 타입을 입력해도 LocalLoginStrategy를 반환한다(대소문자 무관)")
    fun `대소문자 무관 전략 조회`() {
        val strategy = factory.getStrategy("LOCAL")

        assertThat(strategy).isInstanceOf(LocalLoginStrategy::class.java)
    }

    @Test
    @DisplayName("혼합 대소문자 'Kakao' 타입으로도 KakaoLoginStrategy를 반환한다")
    fun `혼합 대소문자 kakao 전략 조회`() {
        val strategy = factory.getStrategy("Kakao")

        assertThat(strategy).isInstanceOf(KakaoLoginStrategy::class.java)
    }

    @Test
    @DisplayName("지원하지 않는 타입으로 조회 시 IllegalArgumentException이 발생한다")
    fun `지원하지 않는 타입 예외 발생`() {
        assertThatThrownBy { factory.getStrategy("github") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Unsupported login type: github")
    }

    @Test
    @DisplayName("빈 문자열 타입으로 조회 시 IllegalArgumentException이 발생한다")
    fun `빈 문자열 타입 예외 발생`() {
        assertThatThrownBy { factory.getStrategy("") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    @DisplayName("null 대신 공백 문자열 타입으로 조회 시 예외가 발생한다")
    fun `공백 문자열 타입 예외 발생`() {
        assertThatThrownBy { factory.getStrategy("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
