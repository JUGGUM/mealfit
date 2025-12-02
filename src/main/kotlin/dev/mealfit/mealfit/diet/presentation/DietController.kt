package dev.mealfit.mealfit.diet.presentation

import dev.mealfit.mealfit.diet.application.event.DietRequestEvent
import dev.mealfit.mealfit.user.domain.UserHealthProfile
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diet")
class DietController(
    private val kafkaTemplate: KafkaTemplate<String, DietRequestEvent>
) {
    @PostMapping("/recommend")
    fun recommend(@RequestBody profile: UserHealthProfile): ResponseEntity<String> {
        kafkaTemplate.send("diet.requested", DietRequestEvent(profile))
        return ResponseEntity.ok("Recommendation requested. Result will be published.")
    }
}
