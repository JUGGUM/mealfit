package dev.mealfit.mealfit.user.presentation

import dev.mealfit.mealfit.user.application.ports.out.UserResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/user")
class UserController {
    // 외부 api와 연결되는 컨트롤러 클래스입니다.
    @GetMapping("/list")
    fun findUserList(): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(UserResponse("gayoung", "1"))
    }
}