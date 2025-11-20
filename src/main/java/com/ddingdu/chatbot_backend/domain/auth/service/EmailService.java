package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.domain.auth.entity.EmailVerification;
import com.ddingdu.chatbot_backend.domain.auth.repository.EmailVerificationRepository;
import java.security.SecureRandom;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이메일 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private static final String MJU_EMAIL_DOMAIN = "@mju.ac.kr";
    private static final SecureRandom random = new SecureRandom();

    /**
     * 이메일 도메인 검증
     */
    public void validateMjuEmail(String email) {
        if (!email.endsWith(MJU_EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("명지대학교 이메일(@mju.ac.kr)만 사용 가능합니다.");
        }
    }

    /**
     * 인증 코드 전송
     */
    @Transactional
    public void sendVerificationCode(String email) {
        // 1. 이메일 도메인 검증
        validateMjuEmail(email);

        // 2. 기존 인증 코드 삭제
        if (emailVerificationRepository.existsByEmail(email)) {
            emailVerificationRepository.deleteByEmail(email);
        }

        // 3. 6자리 인증 코드 생성
        String code = generateVerificationCode();

        // 4. Redis에 저장 (TTL: 5분)
        EmailVerification verification = EmailVerification.builder()
            .id(UUID.randomUUID().toString())
            .email(email)
            .code(code)
            .verified(false)
            .build();

        emailVerificationRepository.save(verification);

        // 5. 이메일 전송
        sendEmail(email, code);

        log.info("인증 코드 전송 완료: email={}", email);
    }

    /**
     * 인증 코드 검증
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("인증 코드가 만료되었거나 존재하지 않습니다."));

        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        // 인증 완료 표시
        EmailVerification verified = EmailVerification.builder()
            .id(verification.getId())
            .email(verification.getEmail())
            .code(verification.getCode())
            .verified(true)
            .build();

        emailVerificationRepository.save(verified);

        log.info("이메일 인증 완료: email={}", email);
        return true;
    }

    /**
     * 인증 완료 여부 확인
     */
    public boolean isVerified(String email) {
        return emailVerificationRepository.findByEmail(email)
            .map(EmailVerification::getVerified)
            .orElse(false);
    }

    /**
     * 인증 정보 삭제
     */
    @Transactional
    public void deleteVerification(String email) {
        if (emailVerificationRepository.existsByEmail(email)) {
            emailVerificationRepository.deleteByEmail(email);
        }
    }

    /**
     * 6자리 인증 코드 생성
     */
    private String generateVerificationCode() {
        int code = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 이메일 전송
     */
    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[띵듀] 이메일 인증 코드");
        message.setText(String.format(
            "안녕하세요, 띵듀입니다.\n\n" +
                "회원가입을 위한 인증 코드는 다음과 같습니다:\n\n" +
                "인증 코드: %s\n\n" +
                "인증 코드는 5분간 유효합니다.\n" +
                "본인이 요청하지 않았다면 이 메일을 무시하세요.",
            code
        ));

        mailSender.send(message);
    }
}

