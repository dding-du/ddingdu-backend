package com.ddingdu.chatbot_backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequestDto {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@mju\\.ac\\.kr$",
        message = "명지대학교 이메일(@mju.ac.kr)만 사용 가능합니다"
    )
    private String email;
}

