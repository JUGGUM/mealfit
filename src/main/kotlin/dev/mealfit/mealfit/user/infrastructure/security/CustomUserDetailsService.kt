package dev.mealfit.mealfit.user.infrastructure.security

import dev.mealfit.mealfit.user.domai.UserPrincipal
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }

        return UserPrincipal(user)
    }
}