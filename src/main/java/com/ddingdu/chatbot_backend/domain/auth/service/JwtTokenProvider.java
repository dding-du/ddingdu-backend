package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        // Secret Key 초기화
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("email", email)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * JWT 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // ROLE_USER 권한 부여
        return new UsernamePasswordAuthenticationToken(
            claims.getSubject(), // userId
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 토큰 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        Claims claims = parseClaims(token);
        return "access".equals(claims.get("type"));
    }

    /**
     * Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return "refresh".equals(claims.get("type"));
    }
}

