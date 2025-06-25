package dev.mealfit.mealfit.user.application

import dev.mealfit.mealfit.user.application.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("kakao")
class KakaoLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // 카카오 OAuth 처리 로직
        return LoginResult("KAKAO", success = true, userId = "kakao123")
    }
}
