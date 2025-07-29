import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureMockMvc
class SignUpServiceTest {
    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @Mock lateinit var eventPublisher: ApplicationEventPublisher
    @Mock lateinit var redissonClient: RedissonClient
    @Mock lateinit var lock: RLock

    lateinit var signUpService: SignUpService

    @BeforeEach
    fun setup() {
        whenever(redissonClient.getLock(any())).thenReturn(lock)
        signUpService = SignUpService(userRepository, passwordEncoder, eventPublisher, redissonClient)
    }

    @Test
    fun `가입 성공 테스트`() {
        val request = SignUpRequest("test@email.com", "nickname", "password")
        whenever(lock.tryLock(any(), any(), any())).thenReturn(true)
        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        whenever(passwordEncoder.encode(any())).thenReturn("encodedPassword")
        whenever(userRepository.save(any())).thenReturn(
            User(1L, request.email, request.username, "encodedPassword", listOf(Role.USER))
        )

        val result = signUpService.signUp(request)

        assertEquals(request.email, result.email)
        verify(eventPublisher).publishEvent(any())
        verify(lock).unlock()
    }

    @Test
    fun `이미 존재하는 이메일이면 예외 발생`() {
        val request = SignUpRequest("duplicate@email.com", "nick", "pass")
        whenever(lock.tryLock(any(), any(), any())).thenReturn(true)
        whenever(userRepository.existsByEmail(any())).thenReturn(true)

        assertThrows(EmailAlreadyUsedException::class.java) {
            signUpService.signUp(request)
        }
        verify(lock).unlock()
    }
}
