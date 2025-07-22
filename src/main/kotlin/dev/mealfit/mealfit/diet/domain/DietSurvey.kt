package dev.mealfit.mealfit.diet.domain

import dev.mealfit.mealfit.user.domain.User
import jakarta.persistence.*

@Entity
@Table(name = "diet_survey")
class DietSurvey(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User,

    // 1. 기본 정보
    val age: Int,
    val gender: Gender,  // ENUM(MALE, FEMALE, OTHER)

    // 2. 신체 정보
    val heightCm: Double,
    val weightKg: Double,
    val activityLevel: ActivityLevel, // ENUM(LOW, MEDIUM, HIGH)

    // 3. 건강 상태
    val hasDiabetes: Boolean,
    val hasHypertension: Boolean,
    val hasAllergies: Boolean,
    val allergyDetails: String? = null,

    // 4. 식습관
    val isVegetarian: Boolean,
    val mealsPerDay: Int,
    val eatsBreakfast: Boolean,
    val caffeineIntakePerDay: Int, // e.g., in cups

    // 5. 목표
    val goal: DietGoal // ENUM(LOSE_WEIGHT, GAIN_WEIGHT, MAINTAIN)
)
