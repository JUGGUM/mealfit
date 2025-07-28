package dev.mealfit.mealfit.user.application

import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import dev.mealfit.mealfit.user.presentation.dto.UserDto
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    //private val userCacheRepository: UserCacheRepository
) {
//    fun getUserDetail(userId: Long): UserDto {
//        return userCacheRepository.getUser(userId)
//            ?: userRepository.findById(userId).let {
//                val dto = UserDto.from(it)
//                userCacheRepository.setUser(userId, dto)
//                dto
//            }
//    }
}