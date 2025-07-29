package dev.mealfit.mealfit.diet.application

import dev.mealfit.mealfit.diet.domain.ActivityLevel
import dev.mealfit.mealfit.diet.domain.DietGoal
import dev.mealfit.mealfit.diet.domain.DietSurvey
import dev.mealfit.mealfit.diet.domain.Gender
import dev.mealfit.mealfit.diet.infrastructure.persistence.DietSurveyRepository
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DietSurveyService(
    private val dietSurveyRepository: DietSurveyRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createInitialSurvey(userId: Long, name: String) {
//        if (dietSurveyRepository.existsByUserId(userId)) {
//            logger.warn("이미 생성된 설문입니다: $userId")
//            return
//        }

        val user = userRepository.findById(userId).orElseThrow()
        
        val survey = DietSurvey(
            user = user,
            age = 29,
            gender = Gender.FEMALE,
            heightCm = 162.0,
            weightKg = 54.5,
            activityLevel = ActivityLevel.MEDIUM,
            hasDiabetes = false,
            hasHypertension = false,
            hasAllergies = true,
            allergyDetails = "계란",
            isVegetarian = false,
            mealsPerDay = 3,
            eatsBreakfast = true,
            caffeineIntakePerDay = 1,
            goal = DietGoal.MAINTAIN
        )
        dietSurveyRepository.save(survey)
    }
}