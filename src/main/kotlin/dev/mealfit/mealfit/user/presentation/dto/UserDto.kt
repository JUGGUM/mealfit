package dev.mealfit.mealfit.user.presentation.dto

import dev.mealfit.mealfit.user.domain.User
// 	외부 요청/응답 스펙 정의 클라이언트와 직접 주고받는 객체
data class UserDto(
    val id: Long,
    val email: String,
    val username: String
) {
    companion object {
        fun from(user: User): UserDto {
            return UserDto(
                id = user.id!!,
                email = user.email,
                username = user.username
            )
        }
    }
}
