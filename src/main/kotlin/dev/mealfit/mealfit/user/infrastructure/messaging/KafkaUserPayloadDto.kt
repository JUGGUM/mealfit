package dev.mealfit.mealfit.user.infrastructure.messaging

// Kafka 전송용 내부 dto
data class KafkaUserPayloadDto(
    val userId: Long,
    val email: String,
    val nickname: String,
    val joinedAt: Long
)