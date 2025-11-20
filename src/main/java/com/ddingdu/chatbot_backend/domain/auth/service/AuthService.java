package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.domain.auth.dto.request.LoginRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.PasswordChangeRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.RefreshRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.SignUpRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.response.TokenResponseDto;
import com.ddingdu.chatbot_backend.domain.auth.entity.AccessTokenBlacklist;
import com.ddingdu.chatbot_backend.domain.auth.entity.RefreshToken;
import com.ddingdu.chatbot_backend.domain.auth.repository.AccessTokenBlacklistRepository;
import com.ddingdu.chatbot_backend.domain.auth.repository.RefreshTokenRepository;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.repository.UsersRepository;
import com.ddingdu.chatbot_backend.global.exception.CustomException;
import com.ddingdu.chatbot_backend.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    /**
     * Refresh Token 저장 (or 업데이트)
     */
    private void saveRefreshToken(Long userId, String token) {
        refreshTokenRepository.findByUserId(userId)
            .ifPresentOrElse(
                refreshToken -> refreshToken.updateToken(token),
                () -> {
                    RefreshToken newRefreshToken = RefreshToken.builder()
                        .userId(userId)
                        .refreshToken(token)
                        .build();
                    refreshTokenRepository.save(newRefreshToken);
                }
            );
    }

    /**
     * 회원가입
     */
    @Transactional
    public TokenResponseDto signup(SignUpRequestDto request) {
        log.info("회원가입 시작: email={}", request.getEmail());

        // 1. 이메일 인증 확인
        if (!emailService.isVerified(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 2. 중복 검사
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (usersRepository.existsByMjuId(request.getMjuId())) {
            throw new CustomException(ErrorCode.DUPLICATE_MJU_ID);
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 사용자 생성
        Users user = Users.builder()
            .mjuId(request.getMjuId())
            .name(request.getName())
            .email(request.getEmail())
            .password(encodedPassword)
            .major(request.getMajor())
            .build();

        Users savedUser = usersRepository.save(user);

        // 5. 이메일 인증 정보 삭제
        emailService.deleteVerification(request.getEmail());

        // 6. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
            savedUser.getUserId(),
            savedUser.getEmail()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(savedUser.getUserId());

        // 7. Refresh Token DB 저장
        saveRefreshToken(savedUser.getUserId(), refreshToken);

        log.info("회원가입 완료: userId={}, email={}", savedUser.getUserId(), savedUser.getEmail());

        return TokenResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(savedUser.getUserId())
            .email(savedUser.getEmail())
            .name(savedUser.getName())
            .build();
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        log.info("로그인 시도: email={}", request.getEmail());

        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getEmail()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        // 4. Refresh Token DB 저장 (로그인 시 토큰 업데이트)
        saveRefreshToken(user.getUserId(), refreshToken);

        log.info("로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());

        return TokenResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .build();
    }

    /**
     * 토큰 재발급
     */
    @Transactional
    public TokenResponseDto refresh(RefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        // 1. Refresh Token 유효성 검증 (JWT 형식 검증)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token인지 확인 (Type claim 확인)
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        // 3. DB에 저장된 토큰과 일치하는지 확인
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 4. 사용자 조회 (토큰에서 userId 추출)
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 5. 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
            user.getUserId(),
            user.getEmail()
        );
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        // 6. DB에 새로운 Refresh Token 저장 (기존 토큰 업데이트)
        storedRefreshToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(storedRefreshToken);

        log.info("토큰 재발급 완료: userId={}", userId);

        return TokenResponseDto.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .build();
    }

    /**
     * 로그아웃: Access Token 블랙리스트 처리 및 Refresh Token 삭제
     */
    @Transactional
    public void logout(String accessToken) {
        // 1. Refresh Token 삭제 (DB에서 영구적으로 토큰을 무효화)
        Long userId = jwtTokenProvider.getUserId(accessToken);
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh Token 삭제 완료: userId={}", userId);

        // 2. Access Token 블랙리스트 처리 (DB에 저장)
        try {
            LocalDateTime expirationTime = jwtTokenProvider.getExpirationDateTime(accessToken);

            AccessTokenBlacklist blacklistEntry = AccessTokenBlacklist.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
                .build();

            accessTokenBlacklistRepository.save(blacklistEntry);

            log.info("Access Token 블랙리스트 DB 등록 완료: expirationTime={}", expirationTime);

        } catch (Exception e) {
            log.error("로그아웃 중 Access Token 블랙리스트 처리 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(PasswordChangeRequestDto request) {
        log.info("비밀번호 재설정 시작: email={}", request.getEmail());

        // 1. 사용자 조회
        Users user = usersRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 인증 코드 검증
        if (!emailService.isVerified(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 3. 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 4. 비밀번호 변경 (엔티티의 메서드 사용 - 변경 감지)
        user.updatePassword(encodedPassword);

        // 5. 인증 정보 삭제
        emailService.deleteVerification(request.getEmail());

        // 6. 기존 Refresh Token 삭제 (보안을 위해 재로그인 필요)
        refreshTokenRepository.deleteByUserId(user.getUserId());

        log.info("비밀번호 재설정 완료: userId={}", user.getUserId());
    }

    /**
     * 비밀번호 확인 (회원 탈퇴 전 확인용)
     */
    @Transactional(readOnly = true)
    public void verifyPassword(String accessToken, String password) {
        log.info("비밀번호 확인 시작");

        // 1. 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 2. 사용자 조회
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        log.info("비밀번호 확인 완료: userId={}", userId);
    }

    /**
     * 비밀번호 변경 (로그인한 사용자)
     * 주의: 이 메서드 호출 전 반드시 /api/auth/verify-password로 현재 비밀번호를 검증해야 합니다.
     */
    @Transactional
    public void updatePassword(String accessToken, String newPassword, String newPasswordConfirm) {
        log.info("비밀번호 변경 시작");

        // 1. 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 2. 사용자 조회
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 새 비밀번호와 확인 비밀번호 일치 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 4. 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.SAME_AS_CURRENT_PASSWORD);
        }

        // 5. 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 6. 비밀번호 변경 (엔티티의 메서드 사용 - 변경 감지)
        user.updatePassword(encodedPassword);

        // 7. 보안을 위해 기존 Refresh Token 삭제 (재로그인 필요)
        refreshTokenRepository.deleteByUserId(userId);

        log.info("비밀번호 변경 완료: userId={}", userId);
    }
    /**
     * 회원탈퇴
     */
    @Transactional
    public void deleteAccount(String accessToken) {
        log.info("회원탈퇴 시작");

        // 1. 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 2. 사용자 조회
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh Token 삭제 완료: userId={}", userId);

        // 4. Access Token 블랙리스트 등록
        try {
            LocalDateTime expirationTime = jwtTokenProvider.getExpirationDateTime(accessToken);

            AccessTokenBlacklist blacklistEntry = AccessTokenBlacklist.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
                .build();

            accessTokenBlacklistRepository.save(blacklistEntry);
            log.info("Access Token 블랙리스트 등록 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("회원탈퇴 중 Access Token 블랙리스트 처리 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 5. 사용자 삭제
        usersRepository.delete(user);

        log.info("회원탈퇴 완료: userId={}, email={}", userId, user.getEmail());
    }
}