package dev.mealfit.mealfit.ai.infrastructure

import dev.mealfit.mealfit.diet.domain.DietSurvey
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

@Component
class OpenAiDietClient(chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    fun generateRecommendation(survey: DietSurvey): String {
        val prompt = buildPrompt(survey)
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content() ?: throw IllegalStateException("AI 응답이 비어있습니다.")
    }

    private fun buildPrompt(survey: DietSurvey): String = """
        당신은 전문 영양사입니다. 아래 사용자 건강 정보를 바탕으로 하루 식단을 추천해주세요.

        [사용자 정보]
        - 나이: ${survey.age}세
        - 성별: ${survey.gender}
        - 신장: ${survey.heightCm}cm
        - 체중: ${survey.weightKg}kg
        - 활동량: ${survey.activityLevel}
        - 당뇨 여부: ${if (survey.hasDiabetes) "있음" else "없음"}
        - 고혈압 여부: ${if (survey.hasHypertension) "있음" else "없음"}
        - 알레르기: ${if (survey.hasAllergies) survey.allergyDetails ?: "있음" else "없음"}
        - 채식주의자: ${if (survey.isVegetarian) "예" else "아니오"}
        - 하루 식사 횟수: ${survey.mealsPerDay}회
        - 아침 식사 여부: ${if (survey.eatsBreakfast) "예" else "아니오"}
        - 하루 카페인 섭취(잔): ${survey.caffeineIntakePerDay}잔
        - 목표: ${survey.goal}

        [요청 형식]
        아침, 점심, 저녁(+간식) 식단과 각 식단의 예상 칼로리, 영양 포인트를 함께 알려주세요.
        한국어로 답변해주세요.
    """.trimIndent()
}
