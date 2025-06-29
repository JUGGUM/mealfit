package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
// 로그인 전략 선택
interface LoginStrategy {
    fun login(request: LoginRequest): LoginResult
}
