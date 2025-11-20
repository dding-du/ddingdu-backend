package com.ddingdu.chatbot_backend.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(errorCode.getStatus().value())
            .error(errorCode.getStatus().name())
            .message(errorCode.getMessage())
            .path(path)
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String customMessage, String path) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(errorCode.getStatus().value())
            .error(errorCode.getStatus().name())
            .message(customMessage)
            .path(path)
            .build();
    }
}