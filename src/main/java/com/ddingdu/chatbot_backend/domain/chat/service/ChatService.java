package com.ddingdu.chatbot_backend.domain.chat.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RestClient fastApiRestClient;

    @Value("${api.fastapi.base-url}")
    private String aiServerBaseUrl;

    public Flux<String> getAiResponse(String userMessage) {
        final String apiPath = "/search";

        return Flux.create(sink -> {
            try {
                log.info("AI 서버 요청 전송: {}", userMessage);

                // 1. 요청 바디 생성: {"query": "사용자 질문"}
                Map<String, String> requestBody = Map.of("query", userMessage);

                // 2. FastAPI 서버로 POST 요청
                // RestClientConfig에서 baseUrl을 설정했다면 uri(apiPath)만 사용합니다.
                // 만약 RestClientConfig에 baseUrl을 설정하지 않았다면 uri(aiServerBaseUrl + apiPath)를 사용해야 합니다.

                Map response = fastApiRestClient.post()
                    .uri(apiPath)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

                // 3. 응답 파싱 및 유효성 검사
                if (response == null || !response.containsKey("result")) {
                    throw new RuntimeException("AI 서버로부터 유효한 'result' 필드를 받지 못했습니다. 응답: " + response);
                }

                String aiAnswer = (String) response.get("result");
                log.info("AI 응답 수신 완료.");

                // 4. Flux로 감싸서 응답 반환
                sink.next(aiAnswer);
                sink.complete();

            } catch (Exception e) {
                log.error("FastAPI 서버 통신 오류 (URL: {}): {}", aiServerBaseUrl + apiPath, e.getMessage(), e);
                sink.error(new RuntimeException("AI 응답을 받아오지 못했습니다. FastAPI 서버 로그 및 방화벽을 확인하세요.", e));
            }
        });
    }
}
