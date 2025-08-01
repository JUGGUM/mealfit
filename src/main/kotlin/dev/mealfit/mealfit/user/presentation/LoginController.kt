package dev.mealfit.mealfit.user.presentation

import dev.mealfit.mealfit.user.application.login.LoginService
import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/login")
class LoginController(private val loginService: LoginService) {

    @PostMapping("/{type}")
    fun login(
        @PathVariable type: String,
        @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResult> {
        return ResponseEntity.ok(loginService.login(type, request))
    }
}
/**
 * domain: 핵심 비즈니스 로직 (Entity, Value Object, Repository Interface)
 * application: use case 단위 서비스 (Service, Command, Query)
 * infrastructure: DB, 외부 API, 이메일 전송 등 기술 구현체
 * presentation: Controller, REST API
 *
 * LoginController (mealfit-api) → LoginService.login(request)
 *
 * LoginService (mealfit-application) → OauthClientPort.getUserInfo(code)
 *
 * KakaoOauthClient (mealfit-infra)에서 실제 API 요청
 *
 * 결과를 받아 UserRepositoryPort.save(user) 호출
 *
 * UserRepositoryAdapter에서 JPA 통해 저장
 */