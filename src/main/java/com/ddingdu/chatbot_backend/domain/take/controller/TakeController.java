package com.ddingdu.chatbot_backend.domain.take.controller;

import com.ddingdu.chatbot_backend.domain.take.dto.TakeRequestDto;
import com.ddingdu.chatbot_backend.domain.take.dto.TakeResponseDto;
import com.ddingdu.chatbot_backend.domain.take.service.TakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/takes")
@RequiredArgsConstructor
@Tag(name = "수강 API", description = "수강 관련 API")
public class TakeController {

    private final TakeService takeService;

    @Operation(summary = "내 강의 조회", description = "내 수강정보를 조회합니다.")
    @GetMapping("/my-lecture")
    ResponseEntity<List<TakeResponseDto>> myLecture(@PathVariable Long userId) {
        return ResponseEntity.ok(takeService.myLecture(userId));
    }

    @Operation(summary = "강의 보관", description = "수강정보를 추가합니다.")
    @PostMapping
    ResponseEntity<Void> takeLecture(@RequestBody TakeRequestDto takeRequestDto) {
        return ResponseEntity.ok(takeService.takeLecture(takeRequestDto));
    }

    @Operation(summary = "강의 보관 해제", description = "수강정보를 삭제합니다.")
    @DeleteMapping
    ResponseEntity<Void> dropLecture(@RequestBody TakeRequestDto takeRequestDto) {
        return ResponseEntity.ok(takeService.dropLecture(takeRequestDto));
    }

}
