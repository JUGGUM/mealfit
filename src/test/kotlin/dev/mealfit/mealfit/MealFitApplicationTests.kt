package dev.mealfit.mealfit

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import dev.mealfit.mealfit.diet.application.DietSurveyService

@SpringBootTest
@ActiveProfiles("test")
class MealFitApplicationTests {

    @MockitoBean
    private lateinit var dietSurveyService: DietSurveyService

	@Test
	fun contextLoads() {
	}

}
