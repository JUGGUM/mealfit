package dev.mealfit.mealfit.auth.presentation

import dev.mealfit.mealfit.common.security.CustomUserDetailsService
import dev.mealfit.mealfit.common.security.JwtTokenProvider
import dev.mealfit.mealfit.user.application.login.ports.`in`.LoginRequest
import dev.mealfit.mealfit.user.application.login.ports.out.LoginResult
import dev.mealfit.mealfit.user.application.signup.SignUpService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.domai.UserPrincipal
import dev.mealfit.mealfit.user.presentation.dto.UserResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Auth", description = "인증 관련 API") // Swagger 문서화용 태그
@RestController
@RequestMapping("/api/auth") // 인증 관련 API는 /api/auth 경로로 설정
class AuthController(
    private val authenticationManager: AuthenticationManager,
    // JWT 토큰 생성을 위한 서비스 (구현 필요)
    private val jwtTokenProvider: JwtTokenProvider,
    private val signUpService: SignUpService,
    private val customUserDetailsService: CustomUserDetailsService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 인증을 위한 로그인 API
     * @param loginRequest 로그인 요청 DTO
     * @return 로그인 결과를 포함한 ResponseEntity
     */
    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResult> {
        try {
            // 1. AuthenticationManager를 사용하여 사용자 인증을 시도합니다.
            // UsernamePasswordAuthenticationToken을 생성하여 인증 요청을 보냅니다.
            logger.info("로그인 요청: username=${loginRequest.username}")
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )

            // 2. 인증 성공 시, SecurityContextHolder에 인증 정보를 설정합니다.
            SecurityContextHolder.getContext().authentication = authentication

            // 3. JWT 토큰을 생성합니다. (jwtTokenProvider가 있다고 가정)
            val principal =
                authentication.principal as UserPrincipal
            val jwt = jwtTokenProvider.generateToken(
                principal.username,
                principal.authorities.map { it.authority })
            // 4. 로그인 응답 DTO를 생성하여 반환합니다.
            return ResponseEntity.ok(
                LoginResult(
                    "common",
                    true,
                    "gayoung",
                    jwt,
                    loginRequest.username
                )
            )
        } catch (e: BadCredentialsException) {
            logger.error("로그인 실패: Bad credentials", e)
            return ResponseEntity.status(401)
                .body(LoginResult("fail", false, "", "", loginRequest.username))
        }
    }

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        val userDto = signUpService.signUp(request)
        val response = UserResponse.from(userDto)
        customUserDetailsService.loadUserByUsername(userDto.username)
        return ResponseEntity.ok(response)
    }
    // 다른 인증 관련 API (예: 회원가입, 비밀번호 재설정 등)를 여기에 추가할 수 있습니다.
}