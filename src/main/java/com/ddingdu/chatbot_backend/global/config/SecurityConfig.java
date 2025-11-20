package com.ddingdu.chatbot_backend.global.config;

import com.ddingdu.chatbot_backend.domain.auth.service.JwtAuthenticationFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용)
            .csrf(AbstractHttpConfigurer::disable)

            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 세션 사용하지 않음 (JWT 사용)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 요청에 대한 인증/인가 설정
            .authorizeHttpRequests(auth -> auth
                // CORS Preflight 요청(OPTIONS)은 무조건 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 정적 리소스
                .requestMatchers("/", "/index.html").permitAll()

                // 인증 없이 접근 가능한 경로 (회원가입, 로그인, 이메일 인증, 비밀번호 재설정)
                .requestMatchers(
                    "/api/auth/signup",
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/auth/email/send",
                    "/api/auth/email/resend",
                    "/api/auth/email/verify",
                    "/api/auth/password/reset-request",
                    "/api/auth/password/reset"
                ).permitAll()

                // 채팅 API (인증 불필요 - 필요시 수정)
                .requestMatchers("/api/chat/**").permitAll()

                // 테스트 API
                .requestMatchers("/api/test/**").permitAll()

                // Swagger 관련 경로
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // 인증 필요 API (로그아웃, 비밀번호 확인, 회원탈퇴)
                .requestMatchers(
                    "/api/auth/logout",
                    "/api/auth/verify-password",
                    "/api/auth/account"
                ).authenticated()

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )

            // JWT 인증 필터 추가
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * 비밀번호 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React 개발 서버
            "https://ddingduroid.vercel.app/",     // 실제 프론트엔드 도메인
            "https://ddingdu.p-e.kr"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

