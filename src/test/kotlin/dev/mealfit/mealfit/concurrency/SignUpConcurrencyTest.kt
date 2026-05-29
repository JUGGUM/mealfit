package dev.mealfit.mealfit.concurrency

import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.application.signup.SignUpService
import dev.mealfit.mealfit.user.application.signup.ports.`in`.SignUpRequest
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:signupdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL"
])
@DisplayName("회원가입 동시성 테스트")
class SignUpConcurrencyTest {

    @Autowired
    private lateinit var signUpService: SignUpService

    @Autowired
    private lateinit var userRepository: UserRepository

    // 비동기 이벤트 처리(DietSurvey 생성)로 인한 FK 충돌 방지
    @MockitoBean
    private lateinit var dietSurveyService: DietSurveyService

    @AfterEach
    fun cleanUp() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("100개 스레드가 같은 이메일로 동시 가입 시도 시 정확히 1건만 저장된다")
    fun `동시 동일 이메일 회원가입 - 1건만 저장`() {
        val threadCount = 100
        val latch = CountDownLatch(threadCount)
        val startGate = CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(threadCount)
        repeat(threadCount) { i ->
            executor.submit {
                try {
                    startGate.await()
                    signUpService.signUp(SignUpRequest("user$i", "pass1234", "same@example.com"))
                    successCount.incrementAndGet()
                } catch (_: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        startGate.countDown()
        latch.await(30, TimeUnit.SECONDS)
        executor.shutdown()

        // 성공 + 실패 = 전체 스레드 수
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount)
        // DB에는 정확히 1건만 저장됨
        assertThat(userRepository.count()).isEqualTo(1L)
        assertThat(userRepository.existsByEmail("same@example.com")).isTrue()
    }

    @Test
    @DisplayName("100개 스레드가 각자 다른 이메일로 동시 가입 시도 시 모두 성공한다")
    fun `동시 고유 이메일 100건 회원가입 - 모두 성공`() {
        val threadCount = 100
        val latch = CountDownLatch(threadCount)
        val startGate = CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(threadCount)
        repeat(threadCount) { i ->
            executor.submit {
                try {
                    startGate.await()
                    signUpService.signUp(SignUpRequest("user$i", "pass1234", "user$i@example.com"))
                    successCount.incrementAndGet()
                } catch (_: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        startGate.countDown()
        latch.await(30, TimeUnit.SECONDS)
        executor.shutdown()

        assertThat(successCount.get()).isEqualTo(threadCount)
        assertThat(failCount.get()).isEqualTo(0)
        assertThat(userRepository.count()).isEqualTo(threadCount.toLong())
    }

    @Test
    @DisplayName("10개 이메일 그룹별로 10개 스레드가 동시 가입 시도 시 그룹당 1건씩 총 10건만 저장된다")
    fun `이메일 그룹별 동시 가입 - 그룹당 1건 저장`() {
        val groupCount = 10
        val threadPerGroup = 10
        val totalThreads = groupCount * threadPerGroup
        val latch = CountDownLatch(totalThreads)
        val startGate = CountDownLatch(1)
        val successCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(totalThreads)
        for (group in 0 until groupCount) {
            repeat(threadPerGroup) { i ->
                executor.submit {
                    try {
                        startGate.await()
                        signUpService.signUp(
                            SignUpRequest("user_g${group}_t$i", "pass1234", "group$group@example.com")
                        )
                        successCount.incrementAndGet()
                    } catch (_: Exception) {
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }

        startGate.countDown()
        latch.await(30, TimeUnit.SECONDS)
        executor.shutdown()

        // 이메일 그룹 수만큼 저장
        assertThat(userRepository.count()).isEqualTo(groupCount.toLong())
    }
}
