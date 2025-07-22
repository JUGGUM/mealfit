package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

open class CustomBaseException(message: String?, errorCode: ErrorCode) : RuntimeException(message) {
    val errorCode: ErrorCode = errorCode
}