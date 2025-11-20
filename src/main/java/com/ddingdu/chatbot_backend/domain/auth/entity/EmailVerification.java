package com.ddingdu.chatbot_backend.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * 이메일 인증 코드 엔티티 (Redis)
 * TTL: 5분 (300초)
 */
@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "email_verification", timeToLive = 300)
public class EmailVerification {

    @Id
    private String id;  // UUID

    @Indexed
    private String email;  // 인증할 이메일 (@mju.ac.kr)

    private String code;   // 6자리 인증 코드

    @Builder.Default
    private Boolean verified = false;  // 인증 완료 여부
}

