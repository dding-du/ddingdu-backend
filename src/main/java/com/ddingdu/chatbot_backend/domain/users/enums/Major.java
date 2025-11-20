package com.ddingdu.chatbot_backend.domain.users.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Major {

    // 응용소프트웨어전공
    APPLIED_SOFTWARE("응용소프트웨어전공"),

    // 데이터사이언스전공
    DATA_SCIENCE("데이터사이언스전공"),

    // 인공지능전공
    AI("인공지능전공"),

    // 디지털콘텐츠디자인학과
    DIGITAL_CONTENT_DESIGN("디지털콘텐츠디자인학과");

    private final String displayName;

    /**
     * JSON 직렬화 시 Enum name 사용
     */
    @JsonValue
    public String getCode() {
        return this.name();
    }

    /**
     * JSON 역직렬화 시 문자열을 Enum으로 변환
     */
    @JsonCreator
    public static Major fromCode(String code) {
        try {
            return Major.valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 전공 코드입니다: " + code);
        }
    }
}
