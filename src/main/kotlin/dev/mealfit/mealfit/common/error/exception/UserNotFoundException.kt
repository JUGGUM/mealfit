package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

class UserNotFoundException : NotFoundException(ErrorCode.USER_NOT_FOUND)
