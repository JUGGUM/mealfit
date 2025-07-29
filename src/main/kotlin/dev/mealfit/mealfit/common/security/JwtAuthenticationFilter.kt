package dev.mealfit.mealfit.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// 이 필터는 HTTP 요청마다 한 번만 실행되도록 보장합니다.
@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    // 필요하다면 UserDetailsService를 주입받아 사용자 정보를 로드할 수 있습니다.
    // private val userDetailsService: UserDetailsService // 주석 처리 또는 제거 가능
) : OncePerRequestFilter() { // OncePerRequestFilter를 상속받아 필터가 요청당 한 번만 실행되도록 함

    // JWT 토큰을 HTTP 요청 헤더에서 추출합니다.
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        // "Bearer " 접두사로 시작하는 토큰을 찾아 반환합니다.
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7) // "Bearer " 다음의 문자열 (토큰) 반환
        }
        return null
    }

    // 실제 필터 로직이 구현되는 메서드입니다.
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // 토큰에서 사용자 ID 또는 이메일 정보 추출 (여기서는 사용자명을 추출한다고 가정)
                val username = jwtTokenProvider.getUsernameFromJWT(jwt)

                // UserDetailsService를 사용하여 사용자 정보를 로드 (필요하다면)
                // val userDetails = userDetailsService.loadUserByUsername(username)

                // 여기서는 토큰의 정보만으로 인증 객체를 생성하는 예시입니다.
                // 실제 프로젝트의 UserDetails 구현체에 맞게 수정해야 합니다.
                // 단순한 인증을 위해 사용자명만 사용하거나, UserDetailsService를 통해 상세 정보를 로드합니다.
                val authentication = UsernamePasswordAuthenticationToken(
                    username, // principal (사용자 정보)
                    null,     // credentials (비밀번호 - JWT 인증에서는 필요 없음)
                    // userDetails?.authorities ?: emptyList() // 권한 정보 (UserDetailsService 사용 시)
                    jwtTokenProvider.getAuthoritiesFromJWT(jwt) // 토큰에서 직접 권한을 얻는 경우
                )

                // 요청에 대한 상세 정보 설정 (IP 주소, 세션 ID 등)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // SecurityContextHolder에 인증 정보 설정 (현재 스레드에서 인증된 사용자 정보에 접근 가능)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (ex: Exception) {
            // JWT 유효성 검사 실패 또는 파싱 오류 등 예외 발생 시
            // 이 곳에서 로그를 남기거나, CustomAuthenticationEntryPoint로 예외를 전달할 수 있습니다.
            logger.error("Could not set user authentication in security context", ex)
            // 에러 응답을 직접 설정하거나, 스프링 시큐리티의 ExceptionHandling으로 전달 가능
            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.message)
            // return // 필터 체인 진행을 막음
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response)
    }
}