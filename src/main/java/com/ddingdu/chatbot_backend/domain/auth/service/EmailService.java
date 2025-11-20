package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.domain.auth.entity.EmailVerification;
import com.ddingdu.chatbot_backend.domain.auth.repository.EmailVerificationRepository;
import com.ddingdu.chatbot_backend.global.exception.CustomException;
import com.ddingdu.chatbot_backend.global.exception.ErrorCode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
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
    private static final int EXPIRATION_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60; // 재전송 쿨다운 1분

    /**
     * 이메일 도메인 검증
     */
    public void validateMjuEmail(String email) {
        if (!email.endsWith(MJU_EMAIL_DOMAIN)) {
            throw new CustomException(ErrorCode.INVALID_MJU_EMAIL);
        }
    }

    /**
     * 인증 코드 전송
     */
    @Transactional
    public void sendVerificationCode(String email) {
        // 1. 이메일 도메인 검증
        validateMjuEmail(email);

        // 2. 기존 인증 코드 조회
        Optional<EmailVerification> existingOpt = emailVerificationRepository.findByEmail(email);

        if (existingOpt.isPresent()) {
            EmailVerification existing = existingOpt.get();
            LocalDateTime lastSentTime = existing.getCreatedAt();
            LocalDateTime cooldownEndTime = lastSentTime.plusSeconds(RESEND_COOLDOWN_SECONDS);

            // 쿨다운 체크
            if (LocalDateTime.now().isBefore(cooldownEndTime)) {
                throw new CustomException(ErrorCode.VERIFICATION_TOO_SOON);
            }

            // 삭제 후 flush로 즉시 DB 반영
            emailVerificationRepository.deleteByEmail(email);
            emailVerificationRepository.flush();
        }

        // 3. 6자리 인증 코드 생성
        String code = generateVerificationCode();

        // 4. DB에 저장
        EmailVerification verification = EmailVerification.builder()
            .email(email)
            .code(code)
            .verified(false)
            .build();

        emailVerificationRepository.save(verification);

        // 5. 이메일 전송
        sendEmail(email, code, "회원가입");

        log.info("인증 코드 전송 완료: email={}", email);
    }

    /**
     * 인증 코드 재전송
     */
    @Transactional
    public void resendVerificationCode(String email) {
        // 재전송도 동일한 로직 사용 (쿨다운 체크 포함)
        sendVerificationCode(email);
        log.info("인증 코드 재전송 완료: email={}", email);
    }

    /**
     * 비밀번호 재설정을 위한 인증 코드 전송
     */
    @Transactional
    public void sendPasswordResetCode(String email) {
        // 1. 이메일 도메인 검증
        validateMjuEmail(email);

        // 2. 기존 인증 코드 삭제
        if (emailVerificationRepository.existsByEmail(email)) {
            emailVerificationRepository.deleteByEmail(email);
            emailVerificationRepository.flush();
        }

        // 3. 6자리 인증 코드 생성
        String code = generateVerificationCode();

        // 4. DB에 저장
        EmailVerification verification = EmailVerification.builder()
            .email(email)
            .code(code)
            .verified(false)
            .build();

        emailVerificationRepository.save(verification);

        // 5. 이메일 전송
        sendEmail(email, code, "비밀번호 재설정");

        log.info("비밀번호 재설정 인증 코드 전송 완료: email={}", email);
    }

    /**
     * 인증 코드 검증
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        // 1. 인증 코드 만료 시간 검증 (5분)
        if (verification.getCreatedAt().plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now())) {
            emailVerificationRepository.delete(verification); // 만료된 코드는 삭제
            throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // 2. 인증 코드 일치 확인
        if (!verification.getCode().equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 3. 인증 완료 표시 (update)
        EmailVerification verified = verification.toBuilder()
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
            .filter(verification -> {
                // 만료 시간 체크
                if (verification.getCreatedAt().plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now())) {
                    emailVerificationRepository.delete(verification);
                    return false; // 만료됨
                }
                return true; // 만료되지 않음
            })
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
    private void sendEmail(String to, String code, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(String.format("[띵듀로이드] %s 이메일 인증 안내", purpose));

            String text = String.format(
                "안녕하세요, 명지대학교 강의정보 챗봇 띵듀로이드입니다.\n" +
                    "회원가입 또는 비밀번호 찾기를 위해 요청하신 인증번호를 보내드립니다.\n\n" +

                    "인증 코드: %s\n\n" +

                    "위 인증번호 숫자 6자리를 인증번호 입력창에 입력해주세요.\n\n" +

                    "⚠️ 주의 사항\n" +
                    "1. 인증번호는 발송 시점으로부터 5분 이내에 입력해 주셔야 유효합니다.\n" +
                    "2. 제한 시간이 초과된 경우, '인증번호 재전송'을 눌러 새로운 인증번호를 받아주세요.\n" +
                    "3. 본 메일은 발송 전용 메일이므로 회신이 불가능합니다.\n\n" +

                    "본인이 인증번호를 요청하지 않았을 경우 본 이메일을 무시해주세요.\n" +
                    "Please ignore this email if you did not request a verification code.\n\n" +

                    "Team Ddingdu Ⓒ All Rights Reserved."
                , code
            );

            message.setText(text);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 전송 실패: email={}, error={}", to, e.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}