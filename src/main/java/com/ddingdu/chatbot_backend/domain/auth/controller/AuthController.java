package com.ddingdu.chatbot_backend.domain.auth.controller;

import com.ddingdu.chatbot_backend.domain.auth.dto.request.EmailRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.EmailVerifyRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.LoginRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.PasswordChangeRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.PasswordResetRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.PasswordVerifyRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.RefreshRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.SignUpRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.response.TokenResponseDto;
import com.ddingdu.chatbot_backend.domain.auth.service.AuthService;
import com.ddingdu.chatbot_backend.domain.auth.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃, 토큰 재발급, 비밀번호 재설정 등 인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다. 이메일 인증이 완료된 상태여야 합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 또는 이메일 미인증"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 학번")
    })
    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDto> signup(
        @Valid @RequestBody SignUpRequestDto request) {

        log.info("POST /api/auth/signup - 회원가입 요청: email={}", request.getEmail());
        TokenResponseDto response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 이메일 또는 비밀번호"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
        @Valid @RequestBody LoginRequestDto request) {

        log.info("POST /api/auth/login - 로그인 요청: email={}", request.getEmail());
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token"),
        @ApiResponse(responseCode = "404", description = "Refresh Token을 찾을 수 없음")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
        @Valid @RequestBody RefreshRequestDto request) {

        log.info("POST /api/auth/refresh - 토큰 재발급 요청");
        TokenResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다. Access Token이 블랙리스트에 등록됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
        @Parameter(description = "Bearer {accessToken}", required = true)
        @RequestHeader("Authorization") String authorizationHeader) {

        log.info("POST /api/auth/logout - 로그아웃 요청");
        String accessToken = authorizationHeader.substring("Bearer ".length());
        authService.logout(accessToken);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "이메일 인증 코드 전송", description = "회원가입을 위한 이메일 인증 코드를 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일 형식"),
        @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    })
    @PostMapping("/email/send")
    public ResponseEntity<String> sendVerificationCode(
        @Valid @RequestBody EmailRequestDto request) {

        log.info("POST /api/auth/email/send - 인증 코드 전송 요청: email={}", request.getEmail());
        emailService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @Operation(summary = "이메일 인증 코드 재전송", description = "이메일 인증 코드를 재전송합니다. (1분 쿨다운)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 코드 재전송 성공"),
        @ApiResponse(responseCode = "400", description = "재전송 쿨다운 중 (1분 대기 필요)"),
        @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    })
    @PostMapping("/email/resend")
    public ResponseEntity<String> resendVerificationCode(
        @Valid @RequestBody EmailRequestDto request) {

        log.info("POST /api/auth/email/resend - 인증 코드 재전송 요청: email={}", request.getEmail());
        emailService.resendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증 코드가 재전송되었습니다.");
    }

    @Operation(summary = "이메일 인증 코드 검증", description = "전송된 인증 코드를 검증합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 성공"),
        @ApiResponse(responseCode = "400", description = "인증 코드 불일치 또는 만료"),
        @ApiResponse(responseCode = "404", description = "인증 코드를 찾을 수 없음")
    })
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyCode(
        @Valid @RequestBody EmailVerifyRequestDto request) {

        log.info("POST /api/auth/email/verify - 인증 코드 검증 요청: email={}", request.getEmail());
        emailService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정을 위한 인증 코드를 이메일로 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
        @ApiResponse(responseCode = "404", description = "등록되지 않은 이메일"),
        @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
    })
    @PostMapping("/password/reset-request")
    public ResponseEntity<String> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequestDto request) {

        log.info("POST /api/auth/password/reset-request - 비밀번호 재설정 요청: email={}", request.getEmail());
        emailService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok("비밀번호 재설정을 위한 인증 코드가 전송되었습니다.");
    }

    @Operation(summary = "비밀번호 재설정", description = "인증 코드를 검증하고 새로운 비밀번호로 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
        @ApiResponse(responseCode = "400", description = "인증 코드 불일치 또는 유효하지 않은 비밀번호"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
        @Valid @RequestBody PasswordChangeRequestDto request) {

        log.info("POST /api/auth/password/reset - 비밀번호 재설정: email={}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다. 다시 로그인해주세요.");
    }

    @Operation(summary = "비밀번호 확인", description = "회원 탈퇴 전 본인 확인을 위한 비밀번호 검증")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공"),
        @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/verify-password")
    public ResponseEntity<String> verifyPassword(
        @Parameter(description = "Bearer {accessToken}", required = true)
        @RequestHeader("Authorization") String authorizationHeader,
        @Valid @RequestBody PasswordVerifyRequestDto request) {

        log.info("POST /api/auth/verify-password - 비밀번호 확인 요청");
        String accessToken = authorizationHeader.substring("Bearer ".length());
        authService.verifyPassword(accessToken, request.getPassword());
        return ResponseEntity.ok("비밀번호가 확인되었습니다.");
    }

    @Operation(summary = "회원탈퇴", description = "현재 로그인된 사용자의 계정을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount(
        @Parameter(description = "Bearer {accessToken}", required = true)
        @RequestHeader("Authorization") String authorizationHeader) {

        log.info("DELETE /api/auth/account - 회원탈퇴 요청");
        String accessToken = authorizationHeader.substring("Bearer ".length());
        authService.deleteAccount(accessToken);
        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }
}