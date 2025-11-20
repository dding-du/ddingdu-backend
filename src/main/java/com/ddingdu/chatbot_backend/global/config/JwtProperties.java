package com.ddingdu.chatbot_backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 프로퍼티
 * application.yml에서 값을 주입받음
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;              // JWT 시크릿 키
    private Long accessTokenExpiration;  // Access Token 만료 시간 (ms)
    private Long refreshTokenExpiration; // Refresh Token 만료 시간 (ms)
}
