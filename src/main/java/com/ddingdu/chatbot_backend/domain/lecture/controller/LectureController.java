package com.ddingdu.chatbot_backend.domain.lecture.controller;

import com.ddingdu.chatbot_backend.domain.lecture.dto.LectureResponseDto;
import com.ddingdu.chatbot_backend.domain.lecture.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
@Tag(name = "강의 API", description = "강의 조회 관련 API")
public class LectureController {

    private final LectureService lectureService;

    @Operation(summary = "강의 전체 조회", description = "강의 전체 조회를 수행합니다.")
    @GetMapping
    ResponseEntity<List<LectureResponseDto>> getLectures() {
        return ResponseEntity.ok(lectureService.getLectures());
    }

    @Operation(summary = "강의명으로 강의 조회", description = "강의명으로 조회를 수행합니다.")
    @GetMapping("/lecture-name/{lectureName}")
    ResponseEntity<List<LectureResponseDto>> getLecturesByLectureName(@PathVariable String lectureName) {
        return ResponseEntity.ok(lectureService.getLecturesByLectureName(lectureName));
    }

    @Operation(summary = "교수명으로 강의 조회", description = "교수명으로 조회를 수행합니다.")
    @GetMapping("/professor/{professorName}")
    ResponseEntity<List<LectureResponseDto>> getLecturesByProfessor(@PathVariable String professorName) {
        return ResponseEntity.ok(lectureService.getLecturesByProfessor(professorName));
    }

    @Operation(summary = "강의번호로 강의 조회", description = "강의번호로 조회를 수행합니다.")
    @GetMapping("/number/{lectureCode}")
    ResponseEntity<List<LectureResponseDto>> getLecturesByNumber(@PathVariable String lectureCode) {
        return ResponseEntity.ok(lectureService.getLecturesByNumber(lectureCode));
    }







}
