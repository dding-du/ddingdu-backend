package com.ddingdu.chatbot_backend.domain.chat.controller;
//
//import com.ddingdu.chatbot_backend.domain.chat.dto.ChatRequestDto;
//import com.ddingdu.chatbot_backend.domain.chat.entity.ChatMessage;
//import com.ddingdu.chatbot_backend.domain.chat.service.GeminiChatService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//

import com.ddingdu.chatbot_backend.domain.chat.dto.ChatRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 API", description = "AI 챗봇과의 대화 관련 API")
public class ChatController {

    private final ChatService ChatService;

    @Operation(summary = "헬스 체크", description = "ChatController의 상태를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "정상 작동 중")
    @GetMapping("/health")
    public String healthCheck() {
        return "ChatController is up and running!";
    }

    @PostMapping(value = "/stream/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
        @PathVariable String conversationId,
        @RequestBody ChatRequestDto requestDto) {

        // Note: 실제 FastAPI가 스트리밍을 지원하지 않더라도, Flux로 감싸 반환하여 WebFlux 환경을 유지합니다.
        return ChatService.getAiResponse(requestDto);
    }


}
//
//    private final GeminiChatService geminiChatService;
//
//    @Operation(summary = "헬스 체크", description = "ChatController의 상태를 확인합니다.")
//    @ApiResponse(responseCode = "200", description = "정상 작동 중")
//    @GetMapping("/health")
//    public String healthCheck() {
//        return "ChatController is up and running!";
//    }
//
//    @Operation(summary = "스트리밍 채팅", description = "AI 챗봇과 실시간 스트리밍 방식으로 대화합니다. Server-Sent Events(SSE)를 사용합니다.")
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "스트리밍 응답 성공",
//            content = @Content(mediaType = "text/event-stream")),
//        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//        @ApiResponse(responseCode = "500", description = "AI 응답 생성 실패")
//    })
//    @PostMapping(value = "/stream/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> chatStream(
//        @Parameter(description = "대화 ID (UUID 권장)", required = true)
//        @PathVariable String conversationId,
//        @RequestBody ChatRequestDto requestDto) {
//
//        return geminiChatService.generateChatStream(conversationId, requestDto.getMessage());
//    }
//
//    @Operation(summary = "대화 내역 조회", description = "특정 대화의 전체 메시지 내역을 조회합니다.")
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "조회 성공",
//            content = @Content(schema = @Schema(implementation = ChatMessage.class))),
//        @ApiResponse(responseCode = "404", description = "대화를 찾을 수 없음")
//    })
//    @GetMapping("/history/{conversationId}")
//    public List<ChatMessage> getHistory(
//        @Parameter(description = "대화 ID", required = true)
//        @PathVariable String conversationId) {
//
//        return geminiChatService.getChatHistory(conversationId);
//    }
//
//    @Operation(summary = "대화 내역 삭제", description = "특정 대화의 모든 메시지를 삭제하고 새로운 대화를 시작합니다.")
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "삭제 성공"),
//        @ApiResponse(responseCode = "404", description = "대화를 찾을 수 없음")
//    })
//    @DeleteMapping("/history/{conversationId}")
//    public String clearHistory(
//        @Parameter(description = "대화 ID", required = true)
//        @PathVariable String conversationId) {
//
//        geminiChatService.startNewConversation(conversationId);
//        return "대화 내용이 초기화되었습니다.";
//    }
//}