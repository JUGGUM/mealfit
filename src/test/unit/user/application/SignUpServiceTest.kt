import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureMockMvc
class SignUpServiceTest {
    private val mockUserRepository = mockk<UserRepository>()
    private val passwordEncoder = BcryptPasswordEncoder()
    private val service = SignUpService(mockUserRepository, passwordEncoder)

    @Test
    fun `회원가입 성공`() {
        every { mockUserRepository.existsByEmail(any()) } returns false
        every { mockUserRepository.save(any()) } returns User(...)

        val result = service.signUp(SignUpRequest(...))
        assertEquals(result.email, "test@example.com")
    }
}
