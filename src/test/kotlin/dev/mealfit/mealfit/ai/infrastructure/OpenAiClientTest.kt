package dev.mealfit.mealfit.ai.infrastructure

import dev.mealfit.mealfit.diet.domain.DietGoal
import dev.mealfit.mealfit.diet.domain.Gender
import dev.mealfit.mealfit.user.domain.Role
import dev.mealfit.mealfit.user.domain.User
import dev.mealfit.mealfit.user.domain.UserHealthProfile
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OpenAiClientTest(
    @Autowired private val openAiClient: OpenAiClient
) {

    @Test
    fun `식단 추천 요청 테스트`() {
        val dummyUser = User(
            id = 1L,
            email = "test@example.com",
            password = "password123",
            username = "테스트유저",
            roles = listOf(Role.USER),
        )

        val profile = UserHealthProfile(
            user = dummyUser,
            age = 30,
            gender = Gender.MALE,
            heightCm = 175,
            weightKg = 70,
            goal = DietGoal.LOSE_WEIGHT,
            activityLevel = "MODERATE",
            allergies = "peanuts",
            preferredFoods = "chicken, broccoli",
            mealsPerDay = 3
        )

        val recommendation = openAiClient.recommendDiet(profile)

        println("추천 결과: $recommendation")

        // 검증
        assertNotNull(recommendation.breakfast, "아침 식단 추천이 null이면 안 됩니다")
        assertNotNull(recommendation.lunch, "점심 식단 추천이 null이면 안 됩니다")
        assertNotNull(recommendation.dinner, "저녁 식단 추천이 null이면 안 됩니다")
    }
}
