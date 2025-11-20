//package com.ddingdu.chatbot_backend.domain.chat.service;
//
//
//import com.ddingdu.chatbot_backend.domain.chat.entity.ChatMessage;
//import com.ddingdu.chatbot_backend.domain.chat.repository.ChatMessageRepository;
//import com.google.genai.Client;
//import com.google.genai.ResponseStream;
//import com.google.genai.types.Content;
//import com.google.genai.types.GenerateContentConfig;
//import com.google.genai.types.GenerateContentResponse;
//import com.google.genai.types.Part;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import reactor.core.publisher.Flux;
//
//@Slf4j
//@Service
//public class GeminiChatService {
//
//    private final Client geminiClient;
//    private final String modelName;
//    private final ChatMessageRepository chatMessageRepository;
//    private final ChromaService chromaService;
//    private GeminiChatService self;
//
//
//    @Autowired
//    public void setSelf(@Lazy GeminiChatService self) {
//        this.self = self;
//
//    }
//
//
//    public GeminiChatService(
//        Client geminiClient,
//        @Value("${gemini.api.model}") String modelName,
//        ChatMessageRepository chatMessageRepository, ChromaService chromaService) {
//        this.geminiClient = geminiClient;
//        this.modelName = modelName;
//        this.chatMessageRepository = chatMessageRepository;
//        this.chromaService = chromaService;
//    }
//
//    @Value("${gemini.system-instruction}")
//    private String systemInstruction;
//
//    public Flux<String> generateChatStream(String conversationId, String userMessage) {
//
//        return Flux.create(sink -> {
//            StringBuilder responseBuffer = new StringBuilder();
//            try {
//                // DB에서 과거 대화 내역 조회
//                List<ChatMessage> historyEntities = chatMessageRepository
//                    .findByConversationIdOrderByCreatedAtAsc(conversationId);
//
//                // DB 내역을 Gemini SDK Content 형식으로 변환
//                List<Content> contents = convertToContentList(historyEntities);
//
//                String context = chromaService.searchRelatedContext(userMessage);
//
//                if (context == null) context = "";
//
//                if (!context.isEmpty()) {
//                    log.info("RAG 검색 성공: conversationId={}, contextLength={}", conversationId, context.length());
//                } else {
//                    log.info("RAG 검색 결과 없음: conversationId={}", conversationId);
//                }
//
//                String finalPrompt = String.format("""
//                    당신은 수강신청 도우미입니다.
//                    [관련 문서]를 보고 사용자의 질문에 정확하게 답변하세요.
//
//                    [관련 문서]
//                    %s
//
//                    [질문]
//                    %s
//
//                    [답변 지침]
//                    1. 교수님 성함, 과목명 등 고유명사는 문서에 있는 그대로 정확히 말하세요.
//                    2. 문서에 없는 내용은 "정보가 없습니다"라고 하세요.
//                    3. 출처가 되는 강의명을 함께 언급해주세요.
//                    """, context, userMessage);
//
//                // 완성된 프롬프트를 사용자 메시지로 추가
//                Content newUserMessage = Content.builder()
//                    .role("user")
//                    .parts(List.of(Part.fromText(finalPrompt)))
//                    .build();
//                contents.add(newUserMessage);
//
//                GenerateContentConfig config = null;
//
//                if (systemInstruction != null && !systemInstruction.isEmpty()) {
//                    config = GenerateContentConfig.builder()
//                        .systemInstruction(
//                            Content.builder()
//                                .parts(List.of(Part.fromText(systemInstruction)))
//                                .build()
//                        )
//                        .build();
//                }
//
//                try (ResponseStream<GenerateContentResponse> stream =
//                    geminiClient.models.generateContentStream(
//                        modelName,
//                        contents,
//                        config // <-- 여기에 시스템 설정을 넣습니다.
//                    )) {
//
//                    // 스트리밍 응답 처리
//                    for (GenerateContentResponse response : stream) {
//                        String textChunk = response.text();
//                        if (textChunk != null && !textChunk.isEmpty()) {
//                            responseBuffer.append(textChunk);
//                            sink.next(textChunk);
//                        }
//                    }
//
//                    String fullResponse = responseBuffer.toString();
//
//                    // DB에는 긴 프롬프트가 아닌 "원래 질문(userMessage)"을 저장
//                    self.saveMessagesInNewTransaction(conversationId, userMessage, fullResponse);
//
//                    sink.complete();
//
//                } catch (Exception e) {
//                    log.error("Gemini API 스트리밍 중 오류 발생: conversationId={}", conversationId, e);
//                    sink.error(new RuntimeException("AI 응답 생성 실패", e));
//                }
//
//            } catch (Exception e) {
//                log.error("대화 생성 중 오류 발생: conversationId={}", conversationId, e);
//                sink.error(new RuntimeException("대화 처리 실패", e));
//            }
//        });
//    }
//
//    @Transactional(readOnly = true)
//
//    public List<ChatMessage> getChatHistory(String conversationId) {
//        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
//    }
//    @Transactional
//    public void startNewConversation(String conversationId) {
//        chatMessageRepository.deleteByConversationId(conversationId);
//        log.info("새 대화 시작: conversationId={}", conversationId);
//    }
//
//    private List<Content> convertToContentList(List<ChatMessage> messages) {
//        return messages.stream()
//            .map(msg -> Content.builder()
//                .role(msg.getRole())
//                .parts(List.of(Part.fromText(msg.getMessage())))
//                .build())
//            .collect(Collectors.toList());
//    }
//
//    protected void saveChatMessage(String conversationId, String role, String message) {
//
//        ChatMessage chatMessage = ChatMessage.builder()
//            .conversationId(conversationId)
//            .role(role)
//            .message(message)
//            .createdAt(LocalDateTime.now())
//            .build();
//        chatMessageRepository.save(chatMessage);
//        log.debug("메시지 저장 완료: conversationId={}, role={}", conversationId, role);
//
//    }
//
//    @Async
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void saveMessagesInNewTransaction(String conversationId, String userMessage,
//        String aiResponse) {
//        try {
//            saveChatMessage(conversationId, "user", userMessage);
//            saveChatMessage(conversationId, "model", aiResponse);
//            log.info("비동기 메시지 저장 성공 (TID: {}): conversationId={}",
//                Thread.currentThread().getId(), conversationId);
//        } catch (Exception e) {
//            log.error("비동기 메시지 저장 실패 (TID: {}): conversationId={}",
//                Thread.currentThread().getId(), conversationId, e);
//        }
//    }
//}