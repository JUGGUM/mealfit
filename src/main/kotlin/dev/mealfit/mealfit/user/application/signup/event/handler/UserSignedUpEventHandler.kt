package dev.mealfit.mealfit.user.application.signup.event.handler

import dev.mealfit.mealfit.diet.application.DietSurveyService
import dev.mealfit.mealfit.user.domain.events.UserSignedUpEvent
import dev.mealfit.mealfit.user.infrastructure.messaging.KafkaUserPayloadDto
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

// 도메인이벤트를 받아 Kafka로 전송하는 어댑터(Application Layer ←→ Messaging Infrastructure)
@Component
class UserSignedUpEventHandler(
    //private val kafkaTemplate: KafkaTemplate<String, KafkaUserPayloadDto>
    private val dietSurveyService: DietSurveyService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    // DB 트랜잭션 커밋이후에 이벤트 핸들링
    @Async("customExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: UserSignedUpEvent) {
        val payload = KafkaUserPayloadDto(
            userId = event.userId,
            email = event.email,
            nickname = event.nickname,
            joinedAt = System.currentTimeMillis()
        )
        dietSurveyService.createInitialSurvey(payload.userId, payload.nickname)
       // kafkaTemplate.send("user.signed-up", payload)
        logger.info("UserSignedUpEvent handled and sent to Kafka: $payload")
    }
}