package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("naver")
class NaverLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // 카카오 OAuth 처리 로직
        return LoginResult("KAKAO", success = true, userId = "kakao123")
    }
}
