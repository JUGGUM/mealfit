package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

open class NotFoundException(errorCode: ErrorCode) :
    CustomBaseException(errorCode.message, errorCode)