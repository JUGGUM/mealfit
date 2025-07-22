package dev.mealfit.mealfit.user.infrastructure.persistence

import dev.mealfit.mealfit.user.application.login.ports.out.UserRepositoryPort
import dev.mealfit.mealfit.user.domain.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val userRepository: UserRepository // JPA interface
) : UserRepositoryPort {
    override fun save(user: User): User = userRepository.save(user)
    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)
    override fun findById(userId: Long): User? = userRepository.findByIdOrNull(userId)
}