package com.ddingdu.chatbot_backend.domain.take.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TakeResponseDto {

    private Long id;
    private String lectureName;
    private String professorName;
}
