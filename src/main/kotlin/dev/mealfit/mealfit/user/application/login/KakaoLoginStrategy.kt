//package dev.mealfit.mealfit.user.application.login
//
//import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
//import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
//import org.springframework.stereotype.Component
//
//@Component("kakao")
//class KakaoLoginStrategy(
//    private val kakaoOAuthClient: KakaoOAuthClient,
//    private val userRepository: UserRepository
//) : LoginStrategy {
//
//    override fun login(request: LoginRequest): LoginResult {
//        val kakaoToken = kakaoOAuthClient.getToken(request.code)
//        val kakaoUserInfo = kakaoOAuthClient.getUserInfo(kakaoToken)
//
//        val user = userRepository.findOrCreate(kakaoUserInfo)
//
//        val appToken = generateAppToken(user.id)
//
//        return LoginResult(
//            provider = "KAKAO",
//            success = true,
//            userId = user.id,
//            token = appToken,
//            username = user.username
//        )
//    }
//}
//
package dev.mealfit.mealfit.user.application.login

import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.stereotype.Component

@Component("kakao")
class KakaoLoginStrategy : LoginStrategy {
    override fun login(request: LoginRequest): LoginResult {
        // 카카오 OAuth 처리 로직
        return LoginResult(
            "KAKAO", success = true, userId = "kakao123",
            token = TODO(),
            username = TODO()
        )
    }
}
