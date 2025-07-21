package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

open class CustomBaseException(message: String?, errorCode: ErrorCode) : RuntimeException(message) {
    val errorCode: ErrorCode = errorCode
}