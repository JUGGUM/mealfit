package dev.mealfit.mealfit.user.presentation

import dev.mealfit.mealfit.user.application.signup.SignUpService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.infrastructure.security.CustomUserDetailsService
import dev.mealfit.mealfit.user.presentation.dto.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SighUpController (
    private val signUpService: SignUpService,
    private val customUserDetailsService: CustomUserDetailsService
){

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        val userDto = signUpService.signUp(request)
        val response = UserResponse.from(userDto) // 변환 작업
        customUserDetailsService.loadUserByUsername(userDto.username)
        return ResponseEntity.ok(response)
    }
}