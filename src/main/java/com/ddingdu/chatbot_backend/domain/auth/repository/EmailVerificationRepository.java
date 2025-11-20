package com.ddingdu.chatbot_backend.domain.auth.repository;

import com.ddingdu.chatbot_backend.domain.auth.entity.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이메일 인증 코드 Repository
 */
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    /**
     * 이메일로 인증 코드 조회
     */
    Optional<EmailVerification> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 이메일로 삭제
     */
    @Transactional
    void deleteByEmail(String email);
}