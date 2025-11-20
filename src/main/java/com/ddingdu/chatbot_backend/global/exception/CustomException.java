package com.ddingdu.chatbot_backend.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    int errorCode;

    public CustomException(ErrorCode errorCode) {}
}
