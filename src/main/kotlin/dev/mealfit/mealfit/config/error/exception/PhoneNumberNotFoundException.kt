package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

class PhoneNumberNotFoundException : NotFoundException(ErrorCode.PHONE_NUMBER_NOT_FOUND)
