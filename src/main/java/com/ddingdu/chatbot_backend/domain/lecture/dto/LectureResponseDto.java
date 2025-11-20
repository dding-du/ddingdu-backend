package com.ddingdu.chatbot_backend.domain.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponseDto {

    private Long id;
    private String lectureName;
    private String professorName;
    private String lectureCode;

}
