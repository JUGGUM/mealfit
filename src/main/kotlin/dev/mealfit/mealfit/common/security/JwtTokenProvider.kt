package dev.mealfit.mealfit.common.security

import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expiration}") private val expirationMs: Long
) {
    private val key: Key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(username: String, roles: List<String>): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getUsername(token: String): String {
        return getClaims(token).subject
    }

//    fun getAuthentication(token: String): Authentication {
//        val username = getUsername(token)
//        val roles = getClaims(token).get("roles", List::class.java) as List<*>
//        val authorities = roles.map { SimpleGrantedAuthority(it.toString()) }
//        val principal = User(username, "", authorities)
//        return UsernamePasswordAuthenticationToken(principal, token, authorities)
//    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
