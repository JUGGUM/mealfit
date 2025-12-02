package dev.mealfit.mealfit.diet.domain

data class DietRecommendation(
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val snacks: List<String> = emptyList(),
    val totalCalories: Int? = null,
    val protein: Int? = null,
    val carbs: Int? = null,
    val fat: Int? = null)