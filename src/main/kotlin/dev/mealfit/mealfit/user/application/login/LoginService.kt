package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.common.error.exception.InvalidLoginTypeException
import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Service

@Service
class LoginService(private val factory: LoginStrategyFactory) {

    fun login(type: String, request: LoginRequest): LoginResult {
        val strategy = try {
            factory.getStrategy(type)
        } catch (e: IllegalArgumentException) {
            throw InvalidLoginTypeException("지원하지 않는 로그인 타입입니다: $type")
        }

        return strategy.login(request);
    }
}
