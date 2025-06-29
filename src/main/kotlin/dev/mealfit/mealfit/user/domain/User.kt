package dev.mealfit.mealfit.user.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(unique = true, nullable = false)
    val password: String,
    // ... 나머지 필드들
)