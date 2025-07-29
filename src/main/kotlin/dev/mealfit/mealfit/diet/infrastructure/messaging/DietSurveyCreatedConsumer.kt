package dev.mealfit.mealfit.diet.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import dev.mealfit.mealfit.user.infrastructure.messaging.KafkaUserPayloadDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class DietSurveyCreatedConsumer(
    private val dietSurveyService: DietSurveyService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["user.signed-up"])
    fun consume(message: String) {
        logger.info("Received message: $message")
        val payload = objectMapper.readValue(message, KafkaUserPayloadDto::class.java)
        dietSurveyService.createInitialSurvey(payload.userId, payload.nickname)
    }
}
