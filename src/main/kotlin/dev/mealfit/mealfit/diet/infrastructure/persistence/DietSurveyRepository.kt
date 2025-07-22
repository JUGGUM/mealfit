package dev.mealfit.mealfit.diet.infrastructure.persistence

import dev.mealfit.mealfit.diet.domain.DietSurvey
import org.springframework.data.jpa.repository.JpaRepository

interface DietSurveyRepository : JpaRepository<DietSurvey, Long> {
}