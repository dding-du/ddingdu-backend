package com.ddingdu.chatbot_backend.domain.chat.controller;

import com.ddingdu.chatbot_backend.domain.chat.dto.ChatRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.entity.ChatMessage;
import com.ddingdu.chatbot_backend.domain.chat.service.GeminiChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ChatController {

    private final GeminiChatService geminiChatService;

    @PostMapping(value = "/stream/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
        @PathVariable String conversationId,
        @RequestBody ChatRequestDto requestDto) {

        return geminiChatService.generateChatStream(conversationId, requestDto.getMessage());
    }

    @GetMapping("/history/{conversationId}")
    public List<ChatMessage> getHistory(@PathVariable String conversationId) {
        return geminiChatService.getChatHistory(conversationId);
    }

    @DeleteMapping("/history/{conversationId}")
    public String clearHistory(@PathVariable String conversationId) {
        geminiChatService.startNewConversation(conversationId);
        return "대화 내용이 초기화되었습니다.";
    }
}
