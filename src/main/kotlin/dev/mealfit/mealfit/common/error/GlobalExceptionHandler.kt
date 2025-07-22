package dev.mealfit.mealfit.common.error

import dev.mealfit.mealfit.common.error.exception.CustomBaseException
import dev.mealfit.mealfit.common.error.exception.PhoneNumberNotFoundException
import dev.mealfit.mealfit.common.error.exception.UserInvalidException
import dev.mealfit.mealfit.common.error.exception.UserNotFoundException
import dev.mealfit.mealfit.config.error.exception.*
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
internal class GlobalExceptionHandler {

    companion object {
        // LoggerFactory를 사용해 정적인 로거 인스턴스 생성
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(CustomBaseException::class)
    protected fun handleCustomBaseException(e: CustomBaseException): ResponseEntity<ErrorResponse?> {
        log.error("CustomBaseException : {}", e.message)
        return createErrorResponseEntity(e.errorCode)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleException(e: Exception): ResponseEntity<ErrorResponse?> {
        log.error("Exception : {}", e.message)
        return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    protected fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException
    ): ResponseEntity<ErrorResponse?> {
        log.error("HttpRequestMethodNotSupportedException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse?> {
        log.error("MethodArgumentNotValidException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotFoundException::class)
    protected fun handleUserNotFoundException(e: UserNotFoundException): ResponseEntity<ErrorResponse?> {
        log.error("UserNotFoundException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.USER_NOT_FOUND)
    }

    @ExceptionHandler(UserInvalidException::class)
    protected fun handleUserInvalidException(e: UserInvalidException): ResponseEntity<ErrorResponse?> {
        log.error("UserInvalidException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.USER_INVALID)
    }

    @ExceptionHandler(PhoneNumberNotFoundException::class)
    protected fun handlePhoneNumberNotFoundException(e: PhoneNumberNotFoundException): ResponseEntity<ErrorResponse?> {
        log.error("PhoneNumberNotFoundException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.PHONE_NUMBER_NOT_FOUND)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    protected fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ErrorResponse?> {
        log.error("NoHandlerFoundException : {}", e.message)
        return createErrorResponseEntity(ErrorCode.URL_NOT_FOUND)
    }

    private fun createErrorResponseEntity(errorCode: ErrorCode): ResponseEntity<ErrorResponse?> {
        return ResponseEntity(
            ErrorResponse.of(errorCode),
            errorCode.status
        )
    }
}