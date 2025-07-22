package dev.mealfit.mealfit.common.error.exception

import dev.mealfit.mealfit.common.error.ErrorCode

class PhoneNumberNotFoundException : NotFoundException(ErrorCode.PHONE_NUMBER_NOT_FOUND)
