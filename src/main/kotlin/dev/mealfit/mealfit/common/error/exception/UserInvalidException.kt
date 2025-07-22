package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

class UserInvalidException : BadRequestException(ErrorCode.USER_INVALID)
