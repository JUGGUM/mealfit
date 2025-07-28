//package dev.mealfit.mealfit.auth.infrastructure.token
//
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.stereotype.Component
//import java.time.Duration
//
//@Component
//class RefreshTokenStore(
//    private val redisTemplate: RedisTemplate<String, String>
//) {
//    private val TTL = Duration.ofDays(7)
//
//    fun saveRefreshToken(userId: Long, refreshToken: String) {
//        redisTemplate.opsForValue().set("refresh:$userId", refreshToken, TTL)
//    }
//
//    fun getRefreshToken(userId: Long): String? {
//        return redisTemplate.opsForValue().get("refresh:$userId")
//    }
//
//    fun deleteRefreshToken(userId: Long) {
//        redisTemplate.delete("refresh:$userId")
//    }
//}
/***
 * 리프레시 토큰을 Redis에 저장하여 재발급과 무효화 관리, 보안 강화
 * fun reissueToken(userId: Long, requestToken: String): TokenResponse {
 *     val savedToken = refreshTokenStore.getRefreshToken(userId)
 *     if (savedToken != requestToken) throw UnauthorizedException()
 *
 *     val newToken = jwtTokenProvider.createToken(userId)
 *     refreshTokenStore.saveRefreshToken(userId, newToken.refreshToken)
 *     return newToken
 * }
 *
 * 인증시 사용예시
 */