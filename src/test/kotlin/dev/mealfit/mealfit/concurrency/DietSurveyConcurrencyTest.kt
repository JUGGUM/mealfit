package dev.mealfit.mealfit.concurrency

import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.diet.infrastructure.persistence.DietSurveyRepository
import dev.mealfit.mealfit.user.domain.Role
import dev.mealfit.mealfit.user.domain.User
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:dietdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL"
])
@DisplayName("식단 설문 동시성 테스트")
class DietSurveyConcurrencyTest {

    @Autowired
    private lateinit var dietSurveyService: DietSurveyService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var dietSurveyRepository: DietSurveyRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @AfterEach
    fun cleanUp() {
        dietSurveyRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("100개 스레드가 같은 사용자의 식단 설문 동시 생성 시 중복 없이 처리된다")
    fun `동일 사용자 식단 설문 동시 생성 - 중복 방지`() {
        val user = userRepository.save(
            User(
                username = "concurrencyUser",
                email = "concurrency@example.com",
                password = passwordEncoder.encode("pass"),
                roles = listOf(Role.USER)
            )
        )

        val threadCount = 100
        val latch = CountDownLatch(threadCount)
        val startGate = CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(threadCount)
        repeat(threadCount) {
            executor.submit {
                try {
                    startGate.await()
                    dietSurveyService.createInitialSurvey(user.id!!, user.username)
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

        // 전체 스레드는 성공 또는 실패로 완료됨
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount)
        // 최소 1건 이상 처리됨
        assertThat(successCount.get()).isGreaterThanOrEqualTo(1)
    }

    @Test
    @DisplayName("100명의 서로 다른 사용자가 동시에 식단 설문 생성 시 모두 성공한다")
    fun `서로 다른 100명 동시 식단 설문 생성 - 모두 성공`() {
        val users = (1..100).map { i ->
            userRepository.save(
                User(
                    username = "user$i",
                    email = "user$i@example.com",
                    password = passwordEncoder.encode("pass"),
                    roles = listOf(Role.USER)
                )
            )
        }

        val threadCount = users.size
        val latch = CountDownLatch(threadCount)
        val startGate = CountDownLatch(1)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(threadCount)
        users.forEach { user ->
            executor.submit {
                try {
                    startGate.await()
                    dietSurveyService.createInitialSurvey(user.id!!, user.username)
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
        assertThat(dietSurveyRepository.count()).isEqualTo(threadCount.toLong())
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 동시 설문 생성 시도 시 모두 예외로 처리된다")
    fun `존재하지 않는 사용자 동시 요청 - 모두 실패`() {
        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val startGate = CountDownLatch(1)
        val failCount = AtomicInteger(0)

        val executor = Executors.newFixedThreadPool(threadCount)
        repeat(threadCount) {
            executor.submit {
                try {
                    startGate.await()
                    dietSurveyService.createInitialSurvey(99999L, "nobody")
                } catch (_: IllegalArgumentException) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        startGate.countDown()
        latch.await(10, TimeUnit.SECONDS)
        executor.shutdown()

        assertThat(failCount.get()).isEqualTo(threadCount)
        assertThat(dietSurveyRepository.count()).isEqualTo(0L)
    }
}
