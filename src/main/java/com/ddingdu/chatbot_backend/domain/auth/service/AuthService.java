package com.ddingdu.chatbot_backend.domain.auth.service;

import com.ddingdu.chatbot_backend.domain.auth.dto.request.LoginRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.RefreshRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.request.SignUpRequestDto;
import com.ddingdu.chatbot_backend.domain.auth.dto.response.TokenResponseDto;
import com.ddingdu.chatbot_backend.domain.auth.entity.AccessTokenBlacklist;
import com.ddingdu.chatbot_backend.domain.auth.entity.RefreshToken;
import com.ddingdu.chatbot_backend.domain.auth.repository.AccessTokenBlacklistRepository;
import com.ddingdu.chatbot_backend.domain.auth.repository.RefreshTokenRepository;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
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
                refreshToken -> refreshToken.updateToken(token), // 기존에 있으면 업데이트
                () -> { // 없으면 새로 저장
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
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        // 2. 중복 검사
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        if (usersRepository.existsByMjuId(request.getMjuId())) {
            throw new IllegalArgumentException("이미 가입된 학번입니다.");
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
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
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
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 2. Refresh Token인지 확인 (Type claim 확인)
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }

        // 3. DB에 저장된 토큰과 일치하는지 확인
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("DB에 존재하지 않는 Refresh Token입니다. (이미 만료 또는 사용됨)"));

        // 4. 사용자 조회 (토큰에서 userId 추출)
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
        // JWT 토큰에서 userId를 추출하여 Refresh Token을 삭제
        Long userId = jwtTokenProvider.getUserId(accessToken);
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh Token 삭제 완료: userId={}", userId);

        // 2. Access Token 블랙리스트 처리 (DB에 저장)
        try {
            // Access Token의 만료 시간을 가져옴
            LocalDateTime expirationTime = jwtTokenProvider.getExpirationDateTime(accessToken);

            // Access Token을 블랙리스트 테이블에 저장
            AccessTokenBlacklist blacklistEntry = AccessTokenBlacklist.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
                .build();

            accessTokenBlacklistRepository.save(blacklistEntry);

            log.info("Access Token 블랙리스트 DB 등록 완료: expirationTime={}", expirationTime);

        } catch (Exception e) {
            log.error("로그아웃 중 Access Token 블랙리스트 처리 실패: {}", e.getMessage());
        }
    }
}
