package dev.mealfit.mealfit.user.application

import dev.mealfit.mealfit.user.application.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.ports.out.LoginResult

interface LoginStrategy {
    fun login(request: LoginRequest): LoginResult
}
