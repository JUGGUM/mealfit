package dev.mealfit.mealfit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class MealFitApplication

fun main(args: Array<String>) {
	runApplication<MealFitApplication>(*args)
}
