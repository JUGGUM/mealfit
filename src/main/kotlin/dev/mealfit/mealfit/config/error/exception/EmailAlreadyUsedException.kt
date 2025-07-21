package dev.mealfit.mealfit.config.error.exception

import dev.mealfit.mealfit.config.error.ErrorCode

open class EmailAlreadyUsedException(errorCode: String) :
    CustomBaseException(
        message = "이미 사용 중인 이메일입니다.",
        errorCode = ErrorCode.EMAIL_ALREADY_USED
    )