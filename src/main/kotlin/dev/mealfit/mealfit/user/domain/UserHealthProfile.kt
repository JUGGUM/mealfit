package dev.mealfit.mealfit.user.domain

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

    // ... 건강 정보 필드들
)