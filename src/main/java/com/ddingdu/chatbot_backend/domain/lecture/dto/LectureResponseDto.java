package com.ddingdu.chatbot_backend.domain.lecture.dto;

import lombok.Setter;

@Setter
public class LectureResponseDto {

    private Long id;
    private String lectureName;
    private String professorName;
    private String lectureCode;

}
