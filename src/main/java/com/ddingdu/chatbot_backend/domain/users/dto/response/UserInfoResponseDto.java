package com.ddingdu.chatbot_backend.domain.users.dto.response;

import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.enums.Major;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponseDto {

    private Long userId;  // pk
    private String mjuId;           // 학번
    private String name;            // 이름
    private String email;           // 이메일
    private Major major;            // 전공 (Enum)
    private String majorName;       // 전공명 (한글)

    public static UserInfoResponseDto from(Users user) {
        return UserInfoResponseDto.builder()
            .userId(user.getUserId())
            .mjuId(user.getMjuId())
            .name(user.getName())
            .email(user.getEmail())
            .major(user.getMajor())
            .majorName(user.getMajor().getDisplayName())
            .build();
    }
}
