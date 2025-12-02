package dev.mealfit.mealfit.ai.application.ports.`in`

import dev.mealfit.mealfit.user.domain.UserHealthProfile

data class AiDietRequest(
    val prompt: String
) {
    companion object {
        fun from(profile: UserHealthProfile): AiDietRequest {
            val prompt = """
                나이: ${profile.age}, 성별: ${profile.gender}, 목표: ${profile.goal},
                알레르기: ${profile.allergies}, 선호 음식: ${profile.preferredFoods},
                하루 식사 횟수: ${profile.mealsPerDay}
                위 정보를 기반으로 하루 식단을 추천해줘.
            """.trimIndent()
            return AiDietRequest(prompt)
        }
    }
}
