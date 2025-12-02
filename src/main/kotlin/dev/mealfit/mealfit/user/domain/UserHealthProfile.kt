package dev.mealfit.mealfit.user.domain

import dev.mealfit.mealfit.diet.domain.DietGoal
import dev.mealfit.mealfit.diet.domain.Gender
import jakarta.persistence.*

@Entity
@Table(name = "user_health_profiles")
data class UserHealthProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val profileId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,


    @Column(nullable = false)
    val age: Int,

    @Column(nullable = false)
    val gender: Gender, // enum class Gender { MALE, FEMALE, OTHER }

    @Column(nullable = false)
    val heightCm: Int,

    @Column(nullable = false)
    val weightKg: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val goal: DietGoal,

    @Column
    val activityLevel: String?, // 예: "LOW", "MODERATE", "HIGH"

    @Column
    val allergies: String?, // 예: "peanuts, dairy"

    @Column
    val preferredFoods: String?, // 예: "chicken, broccoli"

    @Column
    val mealsPerDay: Int = 3
)