package com.ddingdu.chatbot_backend.domain.auth.repository;

import com.ddingdu.chatbot_backend.domain.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * userId로 Refresh Token 조회
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * Refresh Token 값으로 조회
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    /**
     * userId로 삭제
     */
    @Transactional
    void deleteByUserId(Long userId);
}
