package dev.mealfit.mealfit.food.presentation

import dev.mealfit.mealfit.food.application.FoodService
import dev.mealfit.mealfit.food.domain.Food
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FoodController(
    private val foodService: FoodService
) {

    @GetMapping("/foods")
    fun getAllFoods(): List<Food> {
        return foodService.loadFoods()
    }

    @GetMapping("/foods/top-protein")
    fun getTopProteinFoods(@RequestParam(defaultValue = "5") limit: Int): List<Food> {
        return foodService.getTopProteinFoods(limit)
    }
}
