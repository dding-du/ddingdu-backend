package com.ddingdu.chatbot_backend.domain.auth.repository;

import com.ddingdu.chatbot_backend.domain.auth.entity.AccessTokenBlacklist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlacklistRepository extends JpaRepository<AccessTokenBlacklist, Long> {

    /**
     * Access Token 값으로 블랙리스트 조회
     */
    Optional<AccessTokenBlacklist> findByAccessToken(String accessToken);
}
