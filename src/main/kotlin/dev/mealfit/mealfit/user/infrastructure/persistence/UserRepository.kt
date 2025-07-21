package dev.mealfit.mealfit.user.infrastructure.persistence

import dev.mealfit.mealfit.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}