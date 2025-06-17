package dev.mealfit.mealfit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MealFitApplication

fun main(args: Array<String>) {
	runApplication<MealFitApplication>(*args)
}
