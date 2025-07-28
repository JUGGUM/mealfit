//package dev.mealfit.mealfit.common.redis
//
//import dev.mealfit.mealfit.user.presentation.dto.UserDto
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.redis.connection.RedisConnectionFactory
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
//import org.springframework.data.redis.serializer.StringRedisSerializer
//
//@Configuration
//class RedisConfig {
//
//    @Bean
//    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, UserDto> {
//        val template = RedisTemplate<String, UserDto>()
//        template.setConnectionFactory(redisConnectionFactory)
//        template.keySerializer = StringRedisSerializer()
//        template.valueSerializer = GenericJackson2JsonRedisSerializer()
//        return template
//    }
//}