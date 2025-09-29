package dev.mealfit.mealfit.user.application.login.ports.`in`

// 서비스 호출 시 사용 : 내부 비즈니스 로직을 호출하기 위한 입구
data class LoginRequest (
    val email: String,  // 로컬일 경우
    val password: String
)