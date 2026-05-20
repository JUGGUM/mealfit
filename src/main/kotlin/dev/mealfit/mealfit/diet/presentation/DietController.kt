package dev.mealfit.mealfit.diet.presentation

import dev.mealfit.mealfit.diet.application.DietRecommendationService
import dev.mealfit.mealfit.diet.presentation.dto.DietRecommendationResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "Diet", description = "식단 추천 API")
@RestController
@RequestMapping("/api/diet")
class DietController(
    private val dietRecommendationService: DietRecommendationService
) {

    @Operation(summary = "AI 식단 추천 생성", description = "GPT-4o-mini를 이용해 사용자 맞춤 식단을 추천합니다.")
    @PostMapping("/recommend")
    fun recommend(authentication: Authentication): ResponseEntity<DietRecommendationResponse> {
        val recommendation = dietRecommendationService.recommend(authentication.name)
        return ResponseEntity.ok(DietRecommendationResponse.from(recommendation))
    }

    @Operation(summary = "식단 추천 이력 조회")
    @GetMapping("/recommendations")
    fun getRecommendations(authentication: Authentication): ResponseEntity<List<DietRecommendationResponse>> {
        val recommendations = dietRecommendationService.getRecommendations(authentication.name)
        return ResponseEntity.ok(recommendations.map { DietRecommendationResponse.from(it) })
    }
}
