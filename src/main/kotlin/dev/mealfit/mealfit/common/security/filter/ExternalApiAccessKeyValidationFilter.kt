package dev.mealfit.mealfit.common.security.filter

import dev.mealfit.mealfit.common.error.ErrorCode
import dev.mealfit.mealfit.common.error.ErrorResponse
import jakarta.servlet.ServletException
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class ExternalApiAccessKeyValidationFilter : OncePerRequestFilter() {
    //private val giftCardService: GiftCardService? = null

    private val PROTECTED_URLS: List<String> = mutableListOf(
        "/api/gift-card/voucher/*/*",
        "/api/gift-card/voucher/*/*/use",
        "/api/gift-card/voucher/*/*/cancel",
        "/api/gift-card/voucher/*/*/refundAuth",
        "/api/gift-card/voucher/*/*/refundAmt"
    )

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain
    ) {
        MDC.put("clientIp", request.remoteAddr)
        MDC.put("requestUrl", request.requestURL.toString())

        val requestPath = request.requestURI
        val method = request.method

        logger.debug("================>>requestPath $requestPath")
        logger.debug("================>>isProtectedUrl(requestPath) " + isProtectedUrl(requestPath))

        if (isProtectedUrl(requestPath)) {
            val accessKey = request.getHeader("X-Voucher-Access-Key")
            val authCode = request.getHeader("AuthCode")

            MDC.put("X-Voucher-Access-Key", accessKey)

            logger.debug("================>>authCode $authCode")

            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.setHeader("authCode", authCode)

            val objectMapper = com.fasterxml.jackson.databind.ObjectMapper()
            objectMapper.registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())

            if (accessKey == null || accessKey.isEmpty()) {
                val json = objectMapper.writeValueAsString(
                    ErrorResponse(
                        HttpStatus.NO_CONTENT,
                        ErrorCode.ACCESSKEY_ERROR_1
                    )
                )
                response.writer.write(json)
                return
            }

            logger.debug("================>> $accessKey")

//            if (!giftCardService.isValidAccessKey(accessKey)) {
//                val json = objectMapper.writeValueAsString(
//                    ErrorResponse(
//                        org.springframework.http.HttpStatus.NO_CONTENT,
//                        ErrorCode.ACCESSKEY_ERROR_2
//                    )
//                )
//                response.writer.write(json)
//                return
//            }

            logger.debug("================>> $accessKey")
        }
        filterChain.doFilter(request, response)
    }

    private fun isProtectedUrl(requestPath: String): Boolean {
        return PROTECTED_URLS.stream()
            .anyMatch { pattern: String ->
                requestPath.matches(
                    pattern.replace("*", "[^/]+").toRegex()
                )
            }
    }

    override fun destroy() {
        // 정리 코드
    }
}
