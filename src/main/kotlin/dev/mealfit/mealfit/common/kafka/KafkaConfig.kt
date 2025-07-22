package dev.mealfit.mealfit.common.kafka

import dev.mealfit.mealfit.diet.infrastructure.messaging.DietSurveyErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.xml.sax.ErrorHandler

@Configuration
class KafkaConfig {
    @Bean
    fun errorHandler(): ErrorHandler {
        return DietSurveyErrorHandler()
    }
}