package dev.mealfit.mealfit.user.application.login.ports.out

import dev.mealfit.mealfit.user.domain.User

// 외부시스템(DB, 메세지큐, 외부api) 에 접근할때 사용하는 출구정의
interface UserRepositoryPort {
    fun save(user: User): User
    fun existsByEmail(email: String): Boolean
    fun findById(userId: Long): User?
}