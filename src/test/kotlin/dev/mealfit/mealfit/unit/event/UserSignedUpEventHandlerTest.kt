package dev.mealfit.mealfit.unit.event

import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.application.signup.event.handler.UserSignedUpEventHandler
import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@DisplayName("UserSignedUpEventHandler 단위 테스트")
class UserSignedUpEventHandlerTest {

    @Mock
    private lateinit var dietSurveyService: DietSurveyService

    private lateinit var handler: UserSignedUpEventHandler

    @BeforeEach
    fun setUp() {
        handler = UserSignedUpEventHandler(dietSurveyService)
    }

    @Test
    @DisplayName("UserSignedUpEvent 수신 시 DietSurveyService.createInitialSurvey()가 1회 호출된다")
    fun `이벤트 수신 시 식단 설문 생성 호출`() {
        val event = UserSignedUpEvent(userId = 1L, email = "test@example.com", nickname = "testuser")

        handler.handle(event)

        verify(dietSurveyService, times(1)).createInitialSurvey(1L, "testuser")
    }

    @Test
    @DisplayName("이벤트의 userId와 nickname이 서비스에 정확히 전달된다")
    fun `이벤트 파라미터가 서비스에 정확히 전달된다`() {
        val event = UserSignedUpEvent(userId = 42L, email = "user42@example.com", nickname = "user42")

        handler.handle(event)

        verify(dietSurveyService).createInitialSurvey(42L, "user42")
    }

    @Test
    @DisplayName("email 필드는 서비스 호출에 사용되지 않고 nickname만 전달된다")
    fun `email은 서비스 호출에 사용되지 않음 검증`() {
        val event = UserSignedUpEvent(userId = 5L, email = "ignored@example.com", nickname = "correctNick")

        handler.handle(event)

        verify(dietSurveyService).createInitialSurvey(5L, "correctNick")
        verifyNoMoreInteractions(dietSurveyService)
    }

    @Test
    @DisplayName("DietSurveyService에서 예외 발생 시 핸들러가 예외를 전파한다")
    fun `서비스 예외 시 이벤트 핸들러가 예외를 전파한다`() {
        val event = UserSignedUpEvent(userId = 999L, email = "fail@example.com", nickname = "failuser")
        doThrow(IllegalArgumentException("존재하지 않는 사용자입니다: 999"))
            .whenever(dietSurveyService).createInitialSurvey(999L, "failuser")

        assertThatThrownBy { handler.handle(event) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("존재하지 않는 사용자입니다: 999")
    }

    @Test
    @DisplayName("서로 다른 이벤트가 연속으로 수신되면 각각 올바른 파라미터로 서비스를 호출한다")
    fun `복수 이벤트 순차 처리 검증`() {
        val event1 = UserSignedUpEvent(userId = 1L, email = "a@example.com", nickname = "alice")
        val event2 = UserSignedUpEvent(userId = 2L, email = "b@example.com", nickname = "bob")

        handler.handle(event1)
        handler.handle(event2)

        verify(dietSurveyService).createInitialSurvey(1L, "alice")
        verify(dietSurveyService).createInitialSurvey(2L, "bob")
        verifyNoMoreInteractions(dietSurveyService)
    }
}
