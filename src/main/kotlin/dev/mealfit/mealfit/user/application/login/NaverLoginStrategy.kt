package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("naver")
class NaverLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // 네이버 OAuth 처리 로직
        return LoginResult(
            "NAVER", success = true, userId = "naver456",
            token = TODO(),
            username = TODO()
        )
    }
}
