package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("local")
class LocalLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // DB 아이디(이메일)/비번 체크
        return LoginResult(
            "LOCAL", success = true, userId = "local789",
            token = TODO(),
            username = TODO()
        )
    }
}
