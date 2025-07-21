package dev.mealfit.mealfit.user.application.login.ports.out

// 외부시스템(DB, 메세지큐, 외부api) 에 접근할때 사용하는 출구정의
// DB, 외부 API와 연동하는 객체
data class LoginResult(
    val loginType: String,
    val success: Boolean,
    val userId: String?,  // nullable, 로컬 로그인 시에만 사용
    val token: String,
    val username: String
)