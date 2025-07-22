package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

open class BadRequestException(errorCode: ErrorCode) :
    CustomBaseException(errorCode.message, errorCode)