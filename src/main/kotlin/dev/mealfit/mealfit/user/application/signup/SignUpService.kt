package dev.mealfit.mealfit.user.application.signup

import dev.mealfit.mealfit.common.error.exception.EmailAlreadyUsedException
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.domain.Role
import dev.mealfit.mealfit.user.domain.User
import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import dev.mealfit.mealfit.user.presentation.dto.UserDto
import jakarta.transaction.Transactional
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
    private val redissonClient: RedissonClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    //TODO 패스워드 인증번호 확인할때 redis로 설정
    @Transactional
    fun signUp(request: SignUpRequest): UserDto {
        val lockKey = "lock:signup:${request.email}"
        val lock = redissonClient.getLock(lockKey)
        var isLocked = false

        try {
            // 2초 대기 → 락 유지 5초
            isLocked = lock.tryLock(2, 5, TimeUnit.SECONDS)

            if (!isLocked) {
                throw IllegalStateException("다른 가입 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.")
            }

            // 🔐 임계 영역 시작
            if (userRepository.existsByEmail(request.email)) {
                throw EmailAlreadyUsedException("이미 사용 중인 이메일입니다: ${request.email}")
            }

            val user = User(
                email = request.email,
                username = request.username,
                password = passwordEncoder.encode(request.password),
                roles = List<Role>(1) { Role.USER } // 기본 역할은 USER
            )

            val event = UserSignedUpEvent(
                userId = user.id!!,
                email = user.email,
                nickname = user.username
            )
            eventPublisher.publishEvent(event)

            return UserDto.from(user)

        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException("가입 처리 중 중단됨", e)
        } finally {
            if (isLocked) {
                lock.unlock()
            }
        }
    }
}

