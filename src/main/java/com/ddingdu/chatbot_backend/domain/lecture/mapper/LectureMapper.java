package com.ddingdu.chatbot_backend.domain.lecture.mapper;

import com.ddingdu.chatbot_backend.domain.lecture.dto.LectureResponseDto;
import com.ddingdu.chatbot_backend.domain.lecture.entity.Lecture;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    LectureResponseDto toLectureResponseDto(Lecture lecture);

    List<LectureResponseDto> toLectureResponseDtoList(List<Lecture> lectures);
}
