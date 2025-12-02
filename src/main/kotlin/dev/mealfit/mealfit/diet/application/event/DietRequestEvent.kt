package dev.mealfit.mealfit.diet.application.event

import dev.mealfit.mealfit.user.domain.UserHealthProfile

data class DietRequestEvent(
    val userProfile: UserHealthProfile
)