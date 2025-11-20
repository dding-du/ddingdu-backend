package com.ddingdu.chatbot_backend.domain.auth.dto.request;

import com.ddingdu.chatbot_backend.domain.users.enums.Major;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotNull(message = "학번은 필수입니다")
    private String mjuId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 10, message = "이름은 10자 이하여야 합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@mju\\.ac\\.kr$",
        message = "명지대학교 이메일(@mju.ac.kr)만 사용 가능합니다"
    )
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String password;

    @NotNull(message = "전공은 필수입니다")
    private Major major;

    @NotBlank(message = "인증 코드는 필수입니다")
    @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
    private String verificationCode;

}
