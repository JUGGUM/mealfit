package dev.mealfit.mealfit.unit.security

import dev.mealfit.mealfit.common.security.JwtTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private val secret = "test-secret-key-for-testing-purposes-that-is-at-least-64-bytes-long-1234567890abcdef"
    private val expiration = 3_600_000L // 1시간
    private val provider = JwtTokenProvider(secret, expiration)

    @Test
    @DisplayName("username과 roles로 JWT 토큰 3파트(header.payload.signature) 형식으로 생성된다")
    fun `JWT 토큰 생성 성공`() {
        val token = provider.generateToken("test@example.com", listOf("ROLE_USER"))

        assertThat(token).isNotBlank()
        assertThat(token.split(".")).hasSize(3)
    }

    @Test
    @DisplayName("생성된 JWT 토큰에서 username(이메일)을 올바르게 추출한다")
    fun `JWT 토큰에서 username 추출 성공`() {
        val username = "user@mealfit.dev"
        val token = provider.generateToken(username, listOf("ROLE_USER"))

        val extracted = provider.getUsernameFromJWT(token)

        assertThat(extracted).isEqualTo(username)
    }

    @Test
    @DisplayName("유효한 JWT 토큰의 validateToken()이 true를 반환한다")
    fun `유효한 JWT 토큰 검증 성공`() {
        val token = provider.generateToken("test@example.com", listOf("ROLE_USER"))

        assertThat(provider.validateToken(token)).isTrue()
    }

    @Test
    @DisplayName("만료된 JWT 토큰의 validateToken()이 false를 반환한다")
    fun `만료된 JWT 토큰 검증 실패`() {
        val expiredProvider = JwtTokenProvider(secret, -1L) // 이미 만료된 토큰 생성
        val expiredToken = expiredProvider.generateToken("test@example.com", listOf("ROLE_USER"))

        assertThat(provider.validateToken(expiredToken)).isFalse()
    }

    @Test
    @DisplayName("서명이 변조된 JWT 토큰의 validateToken()이 false를 반환한다")
    fun `변조된 JWT 토큰 검증 실패`() {
        val token = provider.generateToken("test@example.com", listOf("ROLE_USER"))
        val tampered = token.dropLast(5) + "XXXXX"

        assertThat(provider.validateToken(tampered)).isFalse()
    }

    @Test
    @DisplayName("완전히 잘못된 형식의 문자열은 validateToken()에서 false를 반환한다")
    fun `잘못된 형식 토큰 검증 실패`() {
        assertThat(provider.validateToken("not.a.valid.jwt.token")).isFalse()
    }

    @Test
    @DisplayName("빈 문자열 토큰은 validateToken()에서 false를 반환한다")
    fun `빈 문자열 토큰 검증 실패`() {
        assertThat(provider.validateToken("")).isFalse()
    }

    @Test
    @DisplayName("다른 secret으로 서명된 토큰은 validateToken()에서 false를 반환한다")
    fun `다른 키로 서명된 토큰 검증 실패`() {
        val otherSecret = "other-secret-key-for-testing-purposes-that-is-at-least-64-bytes-!!!!!!!!!!!!!!!"
        val otherProvider = JwtTokenProvider(otherSecret, expiration)
        val tokenFromOtherProvider = otherProvider.generateToken("test@example.com", listOf("ROLE_USER"))

        assertThat(provider.validateToken(tokenFromOtherProvider)).isFalse()
    }

    @Test
    @DisplayName("생성된 JWT 토큰에서 단일 role을 올바르게 추출한다")
    fun `JWT 토큰에서 단일 role 추출`() {
        val token = provider.generateToken("test@example.com", listOf("ROLE_USER"))

        val authorities = provider.getAuthoritiesFromJWT(token)

        assertThat(authorities).hasSize(1)
        assertThat(authorities[0].authority).isEqualTo("ROLE_USER")
    }

    @Test
    @DisplayName("생성된 JWT 토큰에서 복수 role을 올바르게 추출한다")
    fun `JWT 토큰에서 복수 role 추출`() {
        val roles = listOf("ROLE_USER", "ROLE_ADMIN")
        val token = provider.generateToken("admin@example.com", roles)

        val authorities = provider.getAuthoritiesFromJWT(token)

        assertThat(authorities).hasSize(2)
        assertThat(authorities.map { it.authority }).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN")
    }

    @Test
    @DisplayName("role이 없는 사용자의 토큰에서 권한 목록이 비어있다")
    fun `role 없는 토큰 권한 목록 비어있음`() {
        val token = provider.generateToken("test@example.com", emptyList())

        val authorities = provider.getAuthoritiesFromJWT(token)

        assertThat(authorities).isEmpty()
    }
}
