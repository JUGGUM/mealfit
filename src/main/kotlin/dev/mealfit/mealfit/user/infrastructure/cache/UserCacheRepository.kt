package dev.mealfit.mealfit.user.infrastructure.cache

import dev.mealfit.mealfit.user.presentation.dto.UserDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

//TODO 유저 정보 또는 로그인 세션을 Redis에 캐싱하여 DB 부하 감소 + 성능 개선
@Component
class UserCacheRepository(
    private val redisTemplate: RedisTemplate<String, UserDto>
) {
    private val TTL = Duration.ofMinutes(30)

    fun getUser(userId: Long): UserDto? {
        return redisTemplate.opsForValue().get("user:$userId")
    }

    fun setUser(userId: Long, userDto: UserDto) {
        redisTemplate.opsForValue().set("user:$userId", userDto, TTL)
    }

    fun deleteUser(userId: Long) {
        redisTemplate.delete("user:$userId")
    }
}
