package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

class NoHandlerFoundException : NotFoundException(ErrorCode.URL_NOT_FOUND)
