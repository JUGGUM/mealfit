package dev.mealfit.mealfit.user.application.login

import org.springframework.stereotype.Component

@Component
class LoginStrategyFactory(strategies: List<LoginStrategy>) {

    private val strategyMap: Map<String, LoginStrategy> = strategies.associateBy {
        it::class.annotations
            .filterIsInstance<Component>()
            .first().value
    }

    fun getStrategy(type: String): LoginStrategy {
        return strategyMap[type.lowercase()]
            ?: throw IllegalArgumentException("Unsupported login type: $type")
    }
}
