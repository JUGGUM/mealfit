package dev.mealfit.mealfit.unit.service

import dev.mealfit.mealfit.common.error.exception.EmailAlreadyUsedException
import dev.mealfit.mealfit.user.application.signup.SignUpService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.domain.Role
import dev.mealfit.mealfit.user.domain.User
import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
@DisplayName("SignUpService 단위 테스트")
class SignUpServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    // ApplicationEventPublisher는 @FunctionalInterface → 람다로 이벤트 직접 캡처
    private val publishedEvents = mutableListOf<Any>()
    private val captureEventPublisher = ApplicationEventPublisher { event -> publishedEvents.add(event) }

    private lateinit var signUpService: SignUpService

    @BeforeEach
    fun setUp() {
        publishedEvents.clear()
        signUpService = SignUpService(userRepository, passwordEncoder, captureEventPublisher)
    }

    @Test
    @DisplayName("유효한 이메일과 비밀번호로 회원가입이 성공한다")
    fun `정상 회원가입 성공`() {
        val request = SignUpRequest("testuser", "password123", "test@example.com")
        val savedUser = buildUser(1L, "testuser", "test@example.com")

        whenever(userRepository.existsByEmail("test@example.com")).thenReturn(false)
        doReturn(savedUser).whenever(userRepository).save(any())

        val result = signUpService.signUp(request)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.username).isEqualTo("testuser")
    }

    @Test
    @DisplayName("이미 사용 중인 이메일로 회원가입 시 EmailAlreadyUsedException이 발생한다")
    fun `중복 이메일 회원가입 시 예외 발생`() {
        val request = SignUpRequest("testuser", "password123", "dup@example.com")
        whenever(userRepository.existsByEmail("dup@example.com")).thenReturn(true)

        assertThatThrownBy { signUpService.signUp(request) }
            .isInstanceOf(EmailAlreadyUsedException::class.java)
    }

    @Test
    @DisplayName("이메일 중복 시 userRepository.save()는 절대 호출되지 않는다")
    fun `이메일 중복 시 저장 미수행 검증`() {
        val request = SignUpRequest("testuser", "password123", "dup@example.com")
        whenever(userRepository.existsByEmail("dup@example.com")).thenReturn(true)

        try { signUpService.signUp(request) } catch (_: Exception) {}

        verify(userRepository, never()).save(any())
    }

    @Test
    @DisplayName("회원가입 시 평문 비밀번호가 BCrypt로 인코딩되어 저장된다")
    fun `회원가입 시 비밀번호 BCrypt 인코딩 검증`() {
        val rawPassword = "rawPassword123"
        val request = SignUpRequest("testuser", rawPassword, "test@example.com")
        val userCaptor = ArgumentCaptor.forClass(User::class.java)
        val savedUser = buildUser(1L, "testuser", "test@example.com")

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        doReturn(savedUser).whenever(userRepository).save(userCaptor.capture())

        signUpService.signUp(request)

        val captured = userCaptor.value
        assertThat(captured.password).isNotEqualTo(rawPassword)
        assertThat(passwordEncoder.matches(rawPassword, captured.password)).isTrue()
    }

    @Test
    @DisplayName("회원가입 성공 시 UserSignedUpEvent가 정확히 1회 발행된다")
    fun `회원가입 성공 시 이벤트 1회 발행`() {
        val request = SignUpRequest("testuser", "password123", "test@example.com")
        val savedUser = buildUser(1L, "testuser", "test@example.com")

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        doReturn(savedUser).whenever(userRepository).save(any())

        signUpService.signUp(request)

        assertThat(publishedEvents).hasSize(1)
    }

    @Test
    @DisplayName("발행된 UserSignedUpEvent에 올바른 userId, email, nickname이 담긴다")
    fun `발행된 이벤트 내용 검증`() {
        val request = SignUpRequest("testuser", "password123", "test@example.com")
        val savedUser = buildUser(1L, "testuser", "test@example.com")

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        doReturn(savedUser).whenever(userRepository).save(any())

        signUpService.signUp(request)

        val event = publishedEvents[0] as UserSignedUpEvent
        assertThat(event.userId).isEqualTo(1L)
        assertThat(event.email).isEqualTo("test@example.com")
        assertThat(event.nickname).isEqualTo("testuser")
    }

    @Test
    @DisplayName("회원가입 시 기본 역할이 USER 하나만 설정된다")
    fun `회원가입 기본 역할 USER 검증`() {
        val request = SignUpRequest("testuser", "password123", "test@example.com")
        val userCaptor = ArgumentCaptor.forClass(User::class.java)
        val savedUser = buildUser(1L, "testuser", "test@example.com")

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        doReturn(savedUser).whenever(userRepository).save(userCaptor.capture())

        signUpService.signUp(request)

        val captured = userCaptor.value
        assertThat(captured.roles).containsExactly(Role.USER)
    }

    @Test
    @DisplayName("이메일 중복 시 이벤트가 발행되지 않는다")
    fun `이메일 중복 시 이벤트 미발행`() {
        val request = SignUpRequest("testuser", "password123", "dup@example.com")
        whenever(userRepository.existsByEmail("dup@example.com")).thenReturn(true)

        try { signUpService.signUp(request) } catch (_: Exception) {}

        assertThat(publishedEvents).isEmpty()
    }

    private fun buildUser(id: Long, username: String, email: String) = User(
        id = id,
        username = username,
        email = email,
        password = passwordEncoder.encode("password"),
        roles = listOf(Role.USER)
    )
}
