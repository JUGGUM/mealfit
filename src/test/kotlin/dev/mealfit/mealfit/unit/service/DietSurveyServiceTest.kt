package dev.mealfit.mealfit.unit.service

import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.diet.domain.ActivityLevel
import dev.mealfit.mealfit.diet.domain.DietGoal
import dev.mealfit.mealfit.diet.domain.DietSurvey
import dev.mealfit.mealfit.diet.domain.Gender
import dev.mealfit.mealfit.diet.infrastructure.persistence.DietSurveyRepository
import dev.mealfit.mealfit.user.domain.Role
import dev.mealfit.mealfit.user.domain.User
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

@ExtendWith(MockitoExtension::class)
@DisplayName("DietSurveyService 단위 테스트")
class DietSurveyServiceTest {

    @Mock
    private lateinit var dietSurveyRepository: DietSurveyRepository

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var dietSurveyService: DietSurveyService

    @BeforeEach
    fun setUp() {
        dietSurveyService = DietSurveyService(dietSurveyRepository, userRepository)
    }

    @Test
    @DisplayName("유효한 사용자 ID로 초기 식단 설문이 정상 생성된다")
    fun `초기 식단 설문 생성 성공`() {
        val userId = 1L
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(any())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        verify(dietSurveyRepository, times(1)).save(any())
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 요청 시 IllegalArgumentException이 발생한다")
    fun `존재하지 않는 사용자 ID 예외 발생`() {
        val nonExistentId = 999L
        whenever(userRepository.findById(nonExistentId)).thenReturn(Optional.empty())

        assertThatThrownBy { dietSurveyService.createInitialSurvey(nonExistentId, "nobody") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("존재하지 않는 사용자입니다: $nonExistentId")
    }

    @Test
    @DisplayName("초기 설문의 기본 나이가 29로 설정된다")
    fun `초기 설문 기본 나이 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        assertThat(surveyCaptor.value.age).isEqualTo(29)
    }

    @Test
    @DisplayName("초기 설문의 기본 성별이 FEMALE로 설정된다")
    fun `초기 설문 기본 성별 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        assertThat(surveyCaptor.value.gender).isEqualTo(Gender.FEMALE)
    }

    @Test
    @DisplayName("초기 설문의 기본 신체 정보(키 162cm, 몸무게 54.5kg, 활동량 MEDIUM)가 설정된다")
    fun `초기 설문 기본 신체 정보 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        val survey = surveyCaptor.value
        assertThat(survey.heightCm).isEqualTo(162.0)
        assertThat(survey.weightKg).isEqualTo(54.5)
        assertThat(survey.activityLevel).isEqualTo(ActivityLevel.MEDIUM)
    }

    @Test
    @DisplayName("초기 설문의 기본 건강 상태(당뇨 없음, 고혈압 없음, 계란 알레르기 있음)가 설정된다")
    fun `초기 설문 기본 건강 상태 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        val survey = surveyCaptor.value
        assertThat(survey.hasDiabetes).isFalse()
        assertThat(survey.hasHypertension).isFalse()
        assertThat(survey.hasAllergies).isTrue()
        assertThat(survey.allergyDetails).isEqualTo("계란")
    }

    @Test
    @DisplayName("초기 설문의 기본 식습관(하루 3끼, 아침 먹음, 카페인 1잔, 채식 아님)이 설정된다")
    fun `초기 설문 기본 식습관 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        val survey = surveyCaptor.value
        assertThat(survey.mealsPerDay).isEqualTo(3)
        assertThat(survey.eatsBreakfast).isTrue()
        assertThat(survey.caffeineIntakePerDay).isEqualTo(1)
        assertThat(survey.isVegetarian).isFalse()
    }

    @Test
    @DisplayName("초기 설문의 기본 목표가 MAINTAIN(현상 유지)으로 설정된다")
    fun `초기 설문 기본 목표 검증`() {
        val userId = 1L
        val surveyCaptor = ArgumentCaptor.forClass(DietSurvey::class.java)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(buildUser(userId)))
        doAnswer { it.arguments[0] as DietSurvey }.whenever(dietSurveyRepository).save(surveyCaptor.capture())

        dietSurveyService.createInitialSurvey(userId, "testuser")

        assertThat(surveyCaptor.value.goal).isEqualTo(DietGoal.MAINTAIN)
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID일 때 dietSurveyRepository.save()는 호출되지 않는다")
    fun `사용자 없으면 설문 저장 미수행`() {
        whenever(userRepository.findById(999L)).thenReturn(Optional.empty())

        try { dietSurveyService.createInitialSurvey(999L, "nobody") } catch (_: Exception) {}

        verify(dietSurveyRepository, never()).save(any())
    }

    private fun buildUser(id: Long) = User(
        id = id,
        username = "testuser",
        email = "test@example.com",
        password = "encoded",
        roles = listOf(Role.USER)
    )
}
