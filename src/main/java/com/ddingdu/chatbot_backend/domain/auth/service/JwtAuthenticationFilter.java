package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.domain.auth.entity.AccessTokenBlacklist;
import com.ddingdu.chatbot_backend.domain.auth.repository.AccessTokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 2.1. 블랙리스트 확인 (DB 조회) [신규]
            AccessTokenBlacklist blacklistEntry = accessTokenBlacklistRepository.findByAccessToken(token).orElse(null);

            if (blacklistEntry != null) {
                // DB에 있지만, 만료 시간이 현재 시간보다 이전이라면 블랙리스트에서 무시하고 삭제 (선택적 최적화)
                if (blacklistEntry.getExpirationTime().isBefore(LocalDateTime.now())) {
                    accessTokenBlacklistRepository.delete(blacklistEntry);
                } else {
                    log.warn("블랙리스트에 등록된 토큰입니다: {}", token);
                    filterChain.doFilter(request, response);
                    return; // 블랙리스트 토큰은 인증 불가
                }
            }

            // Access Token인지 확인
            if (jwtTokenProvider.isAccessToken(token)) {
                // 3. 인증 정보를 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 인증 정보 저장, userId: {}",
                    authentication.getName());
            } else {
                log.warn("Access Token이 아닙니다.");
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
