package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

class NoHandlerFoundException : NotFoundException(ErrorCode.URL_NOT_FOUND)
