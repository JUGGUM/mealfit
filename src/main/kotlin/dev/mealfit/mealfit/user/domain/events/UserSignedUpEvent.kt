package dev.mealfit.mealfit.user.domain.events

data class UserSignedUpEvent(
    val userId: Long,
    val email: String,
    val nickname: String
)