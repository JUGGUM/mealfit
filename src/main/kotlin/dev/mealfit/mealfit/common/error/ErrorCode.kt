package dev.mealfit.mealfit.common.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
    val error_status: String,
    val error_code: String
) {
    // 서버 관련 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E1", "서버 에러가 발생했습니다.", "ERROR", "E1"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E2", "잘못된 HTTP 메서드를 호출했습니다.", "ERROR", "E2"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E3", "잘못된 요청입니다", "ERROR", "E3"),

    // 리소스 관련 오류
    NOT_FOUND(HttpStatus.NOT_FOUND, "E4", "존재하지 않는 엔티티입니다.", "ERROR", "E4"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E5", "올바르지 않은 입력값입니다.", "ERROR", "E5"),

    // 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "존재하지 않는 유저입니다.", "ERROR", "MEM001"),
    USER_INVALID(HttpStatus.BAD_REQUEST, "MEM002", "올바르지 않는 유저입니다.", "ERROR", "MEM002"),
    PHONE_NUMBER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "MEM003",
        "해당 유저의 전화번호가 존재하지 않습니다.",
        "ERROR",
        "MEM003"
    ),

    // URL 관련 오류
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "URL001", "요청하신 URL이 존재하지 않습니다.", "ERROR", "URL001"),

    EMAIL_ALREADY_USED(HttpStatus.BAD_REQUEST, "99", "error", "ERROR", "99"),

    ACCESSKEY_ERROR_1(HttpStatus.NO_CONTENT, "10", "액세스 키가 없습니다.", "ERROR", "10"),
}