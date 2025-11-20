package com.ddingdu.chatbot_backend.domain.auth.entity;

import com.ddingdu.chatbot_backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그아웃된 Access Token 블랙리스트 엔티티
 * Access Token의 남은 만료 시간 동안 유효성을 상실하도록 관리합니다.
 */
@Entity
@Table(name = "access_token_blacklist")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccessTokenBlacklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String accessToken;

    // JWT 만료 시간을 저장합니다. 이 시간 이후에는 DB에서 삭제하지 않아도 무시됩니다.
    @Column(name = "expiration_time", nullable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime expirationTime;
}
