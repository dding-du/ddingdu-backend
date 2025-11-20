package com.ddingdu.chatbot_backend.domain.auth.controller;

import com.ddingdu.chatbot_backend.domain.auth.dto.request.EmailRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.EmailVerifyRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.LoginRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.RefreshRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.SignUpRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.response.TokenResponseDto;
import com.ddingdu.chatbot_backend.domain.auth.service.AuthService;
import com.ddingdu.chatbot_backend.domain.auth.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDto> signup(
        @Valid @RequestBody SignUpRequestDto request) {

        log.info("POST /api/auth/signup - 회원가입 요청: email={}", request.getEmail());
        TokenResponseDto response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
        @Valid @RequestBody LoginRequestDto request) {

        log.info("POST /api/auth/login - 로그인 요청: email={}", request.getEmail());
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
        @Valid @RequestBody RefreshRequestDto request) {

        log.info("POST /api/auth/refresh - 토큰 재발급 요청");
        TokenResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
        @RequestHeader("Authorization") String authorizationHeader) {

        log.info("POST /api/auth/logout - 로그아웃 요청");
        String accessToken = authorizationHeader.substring("Bearer ".length());
        authService.logout(accessToken);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    @PostMapping("/email/send")
    public ResponseEntity<String> sendVerificationCode(
        @Valid @RequestBody EmailRequestDto request) {

        log.info("POST /api/auth/email/send - 인증 코드 전송 요청: email={}", request.getEmail());
        emailService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyCode(
        @Valid @RequestBody EmailVerifyRequestDto request) {

        log.info("POST /api/auth/email/verify - 인증 코드 검증 요청: email={}", request.getEmail());
        emailService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
}
