package com.ddingdu.chatbot_backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshRequestDto {

    @NotBlank(message = "Refresh Token은 필수입니다")
    private String refreshToken;
}