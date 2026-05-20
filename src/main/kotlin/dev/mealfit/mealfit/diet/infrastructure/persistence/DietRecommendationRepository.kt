package dev.mealfit.mealfit.diet.infrastructure.persistence

import dev.mealfit.mealfit.diet.domain.DietRecommendation
import org.springframework.data.jpa.repository.JpaRepository

interface DietRecommendationRepository : JpaRepository<DietRecommendation, Long> {
    fun findByDietSurvey_Id(dietSurveyId: Long): List<DietRecommendation>
}
