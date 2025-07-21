package dev.mealfit.mealfit.user.presentation.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val username: String
) {
    companion object {
        fun from(dto: UserDto): UserResponse {
            return UserResponse(
                id = dto.id,
                email = dto.email,
                username = dto.username
            )
        }
    }
}
