package dev.mealfit.mealfit.common.error

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.http.HttpStatus
import java.time.LocalDateTime.now

class ErrorResponse private constructor(code: ErrorCode) {
    @JsonSerialize(using = com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    var timestamp: java.time.LocalDateTime = now()

    var code: String = code.code
    var message: String = code.message
    var status: HttpStatus = code.status

    var error_code: String = code.error_code
    var error_status: String = code.error_status

    constructor(status: HttpStatus?, code: ErrorCode) : this(code)

    companion object {
        fun of(code: ErrorCode): ErrorResponse {
            return ErrorResponse(code)
        }
    }
}
