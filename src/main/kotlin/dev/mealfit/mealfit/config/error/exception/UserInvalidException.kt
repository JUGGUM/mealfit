package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

class UserInvalidException : BadRequestException(ErrorCode.USER_INVALID)
