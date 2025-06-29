package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Service

@Service
class LoginService(private val factory: LoginStrategyFactory) {

    fun login(type: String, request: LoginRequest): LoginResult {
        val strategy = factory.getStrategy(type)
        return strategy.login(request)
    }
}
