package dev.mealfit.mealfit.common.error

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.http.HttpStatus
import java.time.LocalDateTime.now

class ErrorResponse {
    @JsonSerialize(using = com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private var timestamp: java.time.LocalDateTime

    private var code: String
    private var message: String
    private var status: HttpStatus

    private var error_code: String
    private var error_status: String


    private constructor(code: ErrorCode) {
        this.timestamp = now()
        this.status = code.status
        this.code = code.code
        this.message = code.message
        this.error_code = code.error_code
        this.error_status = code.error_status
    }

    constructor(status: HttpStatus?, code: ErrorCode) {
        this.timestamp = now()
        this.status = code.status
        this.code = code.code
        this.message = code.message

        this.error_code = code.error_code
        this.error_status = code.error_status
    }

    companion object {
        fun of(code: ErrorCode): ErrorResponse {
            return ErrorResponse(code)
        }
    }
}