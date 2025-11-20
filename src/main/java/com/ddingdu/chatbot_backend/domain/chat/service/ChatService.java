package com.ddingdu.chatbot_backend.domain.chat.service;

import com.ddingdu.chatbot_backend.domain.chat.dto.ChatRequestDto;
import com.ddingdu.chatbot_backend.domain.take.entity.Take;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.repository.UsersRepository;
import com.ddingdu.chatbot_backend.global.exception.CustomException;
import com.ddingdu.chatbot_backend.global.exception.ErrorCode;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RestClient fastApiRestClient;
    private final WebClient fastApiWebClient;
    private final UsersRepository usersRepository;

    @Value("${api.fastapi.base-url}")
    private String aiServerBaseUrl;

//    public Flux<String> getAiResponse(ChatRequestDto chatRequestDto) {
//
//        Users user = usersRepository.findById(chatRequestDto.getUserId())
//            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        String major = user.getMajor().getDisplayName();
//
//        StringBuilder lectures = new StringBuilder("내가 듣는 전공수업은 ");
//
//        for (Take take : user.getTakes()) {
//            lectures.append(take.getLecture().getLectureName()).append(", ");
//        }
//
//        if (lectures.length() > 0) {
//            lectures.setLength(lectures.length() - 2);
//        }
//
//        String lectureList = lectures.toString();
//
//        String background = "나는 " + major + " 학생이야. " + lectureList + " 수업을 듣고 있어.";
//        String userMessage = background + " 질문: " + chatRequestDto.getMessage();
//
//        final String apiPath = "/search";
//
//        return Flux.create(sink -> {
//            try {
//                log.info("AI 서버 요청 전송: {}", userMessage);
//
//                // 1. 요청 바디 생성: {"query": "사용자 질문"}
//                Map<String, String> requestBody = Map.of("query", userMessage);
//
//                // 2. FastAPI 서버로 POST 요청
//                // RestClientConfig에서 baseUrl을 설정했다면 uri(apiPath)만 사용합니다.
//                // 만약 RestClientConfig에 baseUrl을 설정하지 않았다면 uri(aiServerBaseUrl + apiPath)를 사용해야 합니다.
//
//                Map response = fastApiRestClient.post()
//                    .uri(apiPath)
//                    .body(requestBody)
//                    .retrieve()
//                    .body(Map.class);
//
//                // 3. 응답 파싱 및 유효성 검사
//                if (response == null || !response.containsKey("result")) {
//                    throw new RuntimeException(
//                        "AI 서버로부터 유효한 'result' 필드를 받지 못했습니다. 응답: " + response);
//                }
//
//                String aiAnswer = (String) response.get("result");
//                log.info("AI 응답 수신 완료.");
//
//                // 4. Flux로 감싸서 응답 반환
//                sink.next(aiAnswer);
//                sink.complete();
//
//            } catch (Exception e) {
//                log.error("FastAPI 서버 통신 오류 (URL: {}): {}", aiServerBaseUrl + apiPath,
//                    e.getMessage(), e);
//                sink.error(
//                    new RuntimeException("AI 응답을 받아오지 못했습니다. FastAPI 서버 로그 및 방화벽을 확인하세요.", e));
//            }
//        });
//    }

    public Flux<String> getAiResponse(ChatRequestDto chatRequestDto) {

        // 1. 사용자 배경 정보 생성 (기존 로직 유지)
        Users user = usersRepository.findById(chatRequestDto.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String major = user.getMajor().getDisplayName();
        String lectureList = user.getTakes().stream()
            .map(take -> take.getLecture().getLectureName())
            .collect(Collectors.joining(", "));

        String background = "나는 " + major + " 학생이야. 내가 듣는 전공수업은 " + lectureList + " 수업이야.";
        String userMessage = background + " 질문: " + chatRequestDto.getMessage();

        // 2. 요청 바디 생성: {"query": "사용자 질문"}
        Map<String, String> requestBody = Map.of("query", userMessage);

        log.info("AI 서버 요청 전송: {}", userMessage);

        // 3. WebClient 비동기 호출 및 Flux 반환 (핵심 변경)
        // WebClient는 기본적으로 논블로킹 방식으로 동작합니다.
        return fastApiWebClient.post()
            .uri(aiServerBaseUrl)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.error("FastAPI 서버 오류 응답 수신: {}", errorBody);
                        return Mono.error(new RuntimeException("FastAPI 서버 통신 실패: " + errorBody));
                    }))
            // 응답을 Map으로 받아 "result" 필드만 추출
            .bodyToMono(Map.class)
            .map(response -> {
                if (response == null || !response.containsKey("result")) {
                    throw new RuntimeException("AI 서버로부터 유효한 'result' 필드를 받지 못했습니다. 응답: " + response);
                }
                return (String) response.get("result");
            })
            // 단일 String 결과를 Flux<String>으로 변환
            .flux();
    }
}
