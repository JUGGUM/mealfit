package dev.mealfit.mealfit.diet.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "diet_recommendation")
class DietRecommendation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_survey_id", nullable = false)
    val dietSurvey: DietSurvey,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
