package dev.mealfit.mealfit.diet.application

import dev.mealfit.mealfit.ai.infrastructure.OpenAiClient
import dev.mealfit.mealfit.diet.application.event.DietRequestEvent
import dev.mealfit.mealfit.diet.domain.DietRecommendation
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class DietRequestedConsumer(
    private val openAiClient: OpenAiClient,
    private val kafkaTemplate: KafkaTemplate<String, DietRecommendation>
) {
    @KafkaListener(topics = ["diet.requested"], groupId = "mealfit")
    fun consume(event: DietRequestEvent) {
        val recommendation = openAiClient.recommendDiet(event.userProfile)
        kafkaTemplate.send("diet.recommended", recommendation)
    }
}