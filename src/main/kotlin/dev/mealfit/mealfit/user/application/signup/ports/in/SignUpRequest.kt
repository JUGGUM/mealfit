package dev.mealfit.mealfit.user.application.signup.ports.`in`

// 유저 회원가입 유즈케이스 인터페이스
data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String
)