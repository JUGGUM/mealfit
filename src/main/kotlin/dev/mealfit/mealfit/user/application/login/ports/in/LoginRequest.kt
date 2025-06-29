package dev.mealfit.mealfit.user.application.login.ports.`in`

data class LoginRequest (
    val token: String,     // 소셜일 경우
    val username: String,  // 로컬일 경우
    val password: String
)