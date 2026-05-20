package dev.mealfit.mealfit.diet.presentation.dto

import dev.mealfit.mealfit.diet.domain.DietRecommendation
import java.time.LocalDateTime

data class DietRecommendationResponse(
    val id: Long,
    val content: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(recommendation: DietRecommendation) = DietRecommendationResponse(
            id = recommendation.id!!,
            content = recommendation.content,
            createdAt = recommendation.createdAt
        )
    }
}
