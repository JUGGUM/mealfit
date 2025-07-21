package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

class UserNotFoundException : NotFoundException(ErrorCode.USER_NOT_FOUND)
