package dev.mealfit.mealfit.food.domain

data class Food(
    val name: String,
    val servingSize: String,
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double
)
