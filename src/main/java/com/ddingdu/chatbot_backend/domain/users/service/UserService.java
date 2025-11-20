package com.ddingdu.chatbot_backend.domain.users.service;

import com.ddingdu.chatbot_backend.domain.auth.service.JwtTokenProvider;
import com.ddingdu.chatbot_backend.domain.users.dto.response.UserInfoResponseDto;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.repository.UsersRepository;
import com.ddingdu.chatbot_backend.global.exception.CustomException;
import com.ddingdu.chatbot_backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UsersRepository usersRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 정보 조회
     */
    public UserInfoResponseDto getUserInfo(String accessToken) {
        log.info("사용자 정보 조회 시작");

        // 1. 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 2. 사용자 조회
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        log.info("사용자 정보 조회 완료: userId={}, email={}", userId, user.getEmail());

        // 3. DTO 변환 및 반환
        return UserInfoResponseDto.from(user);
    }
}