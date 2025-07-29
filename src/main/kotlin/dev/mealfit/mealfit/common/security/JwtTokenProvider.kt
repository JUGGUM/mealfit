package dev.mealfit.mealfit.common.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expiration}") private val expirationInMilliseconds: Long
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val key: Key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(username: String, roles: List<String>): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationInMilliseconds)

        return Jwts.builder()
            .setSubject(username) // 토큰의 주체 (여기서는 사용자명)
            .claim("roles", roles) // 커스텀 클레임으로 권한 추가
            .setIssuedAt(now) // 발행 시간
            .setExpiration(expiryDate) // 만료 시간
            .signWith(key, SignatureAlgorithm.HS512) // 서명 (알고리즘과 키 사용)
            .compact() // 토큰 생성
    }

    // JWT 토큰에서 사용자명을 추출하는 메서드 (핵심!)
    fun getUsernameFromJWT(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key) // 서명 키 설정
            .build()
            .parseClaimsJws(token) // JWT 파싱
            .body.subject // subject (주체) 클레임 반환
    }

    // JWT 토큰에서 권한(roles) 정보를 추출하는 메서드
    fun getAuthoritiesFromJWT(token: String): List<GrantedAuthority> {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        // "roles" 클레임을 추출하고 GrantedAuthority 객체 리스트로 변환
        val roles = claims["roles"] as? List<String> ?: emptyList()
        return roles.stream()
            .map { SimpleGrantedAuthority(it) }
            .collect(Collectors.toList())
    }

    // JWT 토큰의 유효성을 검사하는 메서드
    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken)
            return true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature") // 잘못된 JWT 서명
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token") // 유효하지 않은 JWT 토큰
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token") // 만료된 JWT 토큰
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token") // 지원되지 않는 JWT 토큰
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty") // JWT 클레임 문자열이 비어있음
        }
        return false
    }
}
