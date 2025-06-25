package dev.mealfit.mealfit.user.application.ports.out

data class LoginResult(
    val loginType: String,
    val success: Boolean,
    val userId: String?  // nullable, 로컬 로그인 시에만 사용
)