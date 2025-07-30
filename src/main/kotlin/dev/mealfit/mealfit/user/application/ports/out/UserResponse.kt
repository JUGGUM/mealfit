package dev.mealfit.mealfit.user.application.ports.out

data class UserResponse (
    val username: String,  // 로컬일 경우
    val password: String
)