package com.ddingdu.chatbot_backend.domain.users.controller;

import com.ddingdu.chatbot_backend.domain.users.dto.response.UserInfoResponseDto;
import com.ddingdu.chatbot_backend.domain.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 정보 조회 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다. (마이페이지)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(
        @Parameter(description = "Bearer {accessToken}", required = true)
        @RequestHeader("Authorization") String authorizationHeader) {

        log.info("GET /api/users/me - 내 정보 조회 요청");
        String accessToken = authorizationHeader.substring("Bearer ".length());
        UserInfoResponseDto response = userService.getUserInfo(accessToken);
        return ResponseEntity.ok(response);
    }
}