package dev.mealfit.mealfit.diet.application

import dev.mealfit.mealfit.ai.infrastructure.OpenAiDietClient
import dev.mealfit.mealfit.diet.domain.DietRecommendation
import dev.mealfit.mealfit.diet.infrastructure.persistence.DietRecommendationRepository
import dev.mealfit.mealfit.diet.infrastructure.persistence.DietSurveyRepository
import dev.mealfit.mealfit.user.infrastructure.persistence.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DietRecommendationService(
    private val userRepository: UserRepository,
    private val dietSurveyRepository: DietSurveyRepository,
    private val dietRecommendationRepository: DietRecommendationRepository,
    private val openAiDietClient: OpenAiDietClient
) {

    @Transactional
    fun recommend(userEmail: String): DietRecommendation {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다: $userEmail") }
        val survey = dietSurveyRepository.findByUser_Id(user.id!!)
            ?: throw IllegalArgumentException("설문이 등록되지 않은 사용자입니다: $userEmail")

        val content = openAiDietClient.generateRecommendation(survey)
        val recommendation = DietRecommendation(dietSurvey = survey, content = content)
        return dietRecommendationRepository.save(recommendation)
    }

    @Transactional(readOnly = true)
    fun getRecommendations(userEmail: String): List<DietRecommendation> {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다: $userEmail") }
        val survey = dietSurveyRepository.findByUser_Id(user.id!!)
            ?: throw IllegalArgumentException("설문이 등록되지 않은 사용자입니다: $userEmail")
        return dietRecommendationRepository.findByDietSurvey_Id(survey.id!!)
    }
}
