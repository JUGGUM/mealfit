package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("kakao")
class KakaoLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // 카카오 OAuth 처리 로직
        return LoginResult("KAKAO", success = true, userId = "kakao123")
    }
}
