package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

class InvalidLoginTypeException(s: String) : BadRequestException(ErrorCode.INVALID_LOGIN_TYPE) {
}