package dev.mealfit.mealfit.ai.infrastructure

import dev.mealfit.mealfit.ai.application.ports.`in`.AiDietRequest
import dev.mealfit.mealfit.diet.domain.DietRecommendation
import dev.mealfit.mealfit.user.domain.UserHealthProfile
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class OpenAiClient(
    builder: WebClient.Builder
) {
    @Value("\${openai.api.key}")
    private lateinit var apiKey: String

    private val webClient = builder.baseUrl("https://api.openai.com/v1").build()

    fun recommendDiet(profile: UserHealthProfile): DietRecommendation {
        val request = AiDietRequest.from(profile)

        val response = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(
                mapOf(
                    "model" to "gpt-4o-mini",
                    "messages" to listOf(
                        mapOf("role" to "system", "content" to "너는 영양사야."),
                        mapOf("role" to "user", "content" to request.prompt)
                    )
                )
            )
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val content = (response!!["choices"] as List<Map<String, Any>>)[0]["message"] as Map<String, Any>
        val text = content["content"] as String

        return DietRecommendation(
            breakfast = "추천된 아침: $text",
            lunch = "추천된 점심: $text",
            dinner = "추천된 저녁: $text"
        )
    }
}


