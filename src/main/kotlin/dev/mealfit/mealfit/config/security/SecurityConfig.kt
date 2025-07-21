package dev.mealfit.mealfit.config.security

import dev.mealfit.mealfit.config.security.filter.ExternalApiAccessKeyValidationFilter
import dev.mealfit.mealfit.user.infrastructure.security.CustomUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration // 스프링 설정 클래스임을 나타냅니다.
@EnableWebSecurity // 웹 보안을 활성화합니다.
class SecurityConfig(
    // 사용자 정보를 로드하는 커스텀 서비스 (아래에서 구현 예정)
    private val customUserDetailsService: CustomUserDetailsService,
    private val externalApiAccessKeyValidationFilter: ExternalApiAccessKeyValidationFilter
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 1. PasswordEncoder 설정
    // 비밀번호를 안전하게 해싱하고 검증하는 데 사용됩니다.
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // BCryptPasswordEncoder는 강력하고 널리 사용되는 비밀번호 해싱 알고리즘입니다.
        return BCryptPasswordEncoder()
    }

    // 2. AuthenticationManager 설정
    // 스프링 시큐리티 인증 프로세스를 관리하는 핵심 인터페이스입니다.
    // DaoAuthenticationProvider를 사용하여 사용자 인증을 처리합니다.
    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    // 3. DaoAuthenticationProvider 설정
    // UserDetailsService와 PasswordEncoder를 사용하여 사용자 인증을 처리하는 Provider입니다.
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService) // 커스텀 UserDetailsService 주입
        authProvider.setPasswordEncoder(passwordEncoder()) // 비밀번호 인코더 주입
        return authProvider
    }

    // 4. SecurityFilterChain 설정 (핵심 보안 필터 체인 정의)
    // HTTP 요청에 대한 보안 규칙을 정의합니다.
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CORS(Cross-Origin Resource Sharing) 설정을 기본값으로 활성화합니다.
            .cors { it.disable() } // 필요에 따라 .cors { cors -> cors.disable() } 또는 .cors {} 로 설정

            // CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다.
            // REST API에서는 세션 기반 인증이 아니므로 CSRF 보호가 필요하지 않은 경우가 많습니다.
            // 토큰 기반 (JWT 등) 인증을 사용할 경우 비활성화합니다.
            .csrf { it.disable() }

            // HTTP 요청에 대한 접근 권한을 설정합니다.
            .authorizeHttpRequests { auth ->
                auth
                    // /api/auth/** 경로는 인증 없이 누구나 접근 가능하도록 허용합니다.
                    // 회원가입, 로그인 등의 인증 관련 API는 여기에 해당됩니다.
                    .requestMatchers("/api/auth/**").permitAll()
                    // 다른 모든 요청은 인증된 사용자만 접근할 수 있도록 요구합니다.
                    .anyRequest().authenticated()
            }

            // 세션 관리 정책을 설정합니다.
            // REST API와 같은 무상태(stateless) 인증 방식을 사용할 때 유용합니다.
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
            }

            // 폼 로그인 기능을 비활성화합니다. (기본 로그인 페이지를 사용하지 않으므로)
            //.formLogin { formLogin -> formLogin.disable() }

            // HTTP Basic 인증 기능을 비활성화합니다.
            .httpBasic { httpBasic -> httpBasic.disable() }

            // Logout 설정을 정의합니다.
            .logout { logout ->
                logout
                    .logoutUrl("/api/auth/logout") // 로그아웃 요청을 처리할 URL
                    .logoutSuccessHandler { request, response, authentication ->
                        // 로그아웃 성공 시 처리 로직
                        response.status = 200 // OK 상태 코드 반환
                        response.writer.write("Logout successful")
                    }
                    .invalidateHttpSession(true) // 세션 무효화
                    .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제 (세션 사용 시)
            }

            // 예외 처리 설정을 정의합니다.
            .exceptionHandling { exceptionHandling ->
                // 인증되지 않은 사용자가 보호된 리소스에 접근했을 때
                exceptionHandling.authenticationEntryPoint { request, response, authException ->
                    response.status = 401 // Unauthorized 상태 코드 반환
                    response.writer.write("Unauthorized: " + authException.message)
                }
                // 권한이 없는 사용자가 리소스에 접근했을 때
                exceptionHandling.accessDeniedHandler { request, response, accessDeniedException ->
                    response.status = 403 // Forbidden 상태 코드 반환
                    response.writer.write("Forbidden: " + accessDeniedException.message)
                }
            }

        // 인증 프로바이더를 SecurityFilterChain에 추가합니다.
        http.authenticationProvider(authenticationProvider())

        // (선택 사항: JWT 또는 다른 커스텀 필터가 있다면 여기에 추가)
        // 예를 들어, JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가할 수 있습니다.
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build() // SecurityFilterChain 객체를 빌드하여 반환합니다.
    }

//    @Bean
//    fun externalApiAccessKeyValidationFilter(): ExternalApiAccessKeyValidationFilter {
//        log.info("ExternalApiAccessKeyValidationFilter bean created")
//        return externalApiAccessKeyValidationFilter
//    }
}