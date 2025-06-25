package dev.mealfit.mealfit.user.application

import dev.mealfit.mealfit.user.application.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("local")
class LocalLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // DB 아이디/비번 체크
        return LoginResult("LOCAL", success = true, userId = "local789")
    }
}
