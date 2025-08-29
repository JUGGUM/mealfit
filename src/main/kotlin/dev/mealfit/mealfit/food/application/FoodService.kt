package dev.mealfit.mealfit.food.application

import dev.mealfit.mealfit.food.domain.Food
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class FoodService {

    fun loadFoods(): List<Food> {
        val resource = ClassPathResource("static/sample_foods.csv")
        val reader = BufferedReader(InputStreamReader(resource.inputStream))

        return reader.useLines { lines ->
            lines.drop(1) // header 스킵
                .map { line ->
                    val tokens = line.split(",")
                    Food(
                        name = tokens[0],
                        servingSize = tokens[1],
                        calories = tokens[2].toInt(),
                        carbs = tokens[3].toDouble(),
                        protein = tokens[4].toDouble(),
                        fat = tokens[5].toDouble()
                    )
                }
                .toList()
        }
    }

    fun getTopProteinFoods(limit: Int = 5): List<Food> {
        return loadFoods().sortedByDescending { it.protein }.take(limit)
    }
}
