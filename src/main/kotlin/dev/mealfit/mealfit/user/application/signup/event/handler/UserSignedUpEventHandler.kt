package dev.mealfit.mealfit.user.application.signup.event.handler

import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import dev.mealfit.mealfit.user.infrastructure.messaging.KafkaUserPayloadDto
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserSignedUpEventHandler(
    private val kafkaTemplate: KafkaTemplate<String, KafkaUserPayloadDto>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handle(event: UserSignedUpEvent) {
        val payload = KafkaUserPayloadDto(
            userId = event.userId,
            email = event.email,
            nickname = event.nickname,
            joinedAt = System.currentTimeMillis()
        )

        kafkaTemplate.send("user-signed-up-topic", payload)
        logger.info("UserSignedUpEvent handled and sent to Kafka: $payload")
    }
}