package com.ddingdu.chatbot_backend.domain.lecture.service;

import com.ddingdu.chatbot_backend.domain.lecture.dto.LectureResponseDto;
import com.ddingdu.chatbot_backend.domain.lecture.mapper.LectureMapper;
import com.ddingdu.chatbot_backend.domain.lecture.repository.LectureRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureMapper lectureMapper;

    public List<LectureResponseDto> getLectures() {
        return lectureMapper.toLectureResponseDtoList(lectureRepository.findAll());
    }

    public List<LectureResponseDto> getLecturesByLectureName(String lectureName) {
        return lectureMapper.toLectureResponseDtoList(lectureRepository.searchByLectureName(lectureName));
    }

    public List<LectureResponseDto> getLecturesByProfessor(String professorName) {
        return lectureMapper.toLectureResponseDtoList(lectureRepository.findByProfessorNameContaining(professorName));
    }

    public List<LectureResponseDto> getLecturesByNumber(String lectureCode) {
        return lectureMapper.toLectureResponseDtoList(lectureRepository.findByLectureCode(lectureCode));
    }
}
