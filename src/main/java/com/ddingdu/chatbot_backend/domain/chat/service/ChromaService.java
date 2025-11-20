//package com.ddingdu.chatbot_backend.domain.chat.service;
//
//import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaAddRequestDto;
//import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchRequestDto;
//import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchResponseDto;
//import java.util.List;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChromaService {
//
//    private final GeminiEmbeddingService embeddingService;
//
//    @Value("${chroma.url}")
//    private String chromaBaseUrl;
//
//    @Value("${chroma.collection-id}")
//    private String collectionId;
//
//    private final RestClient restClient = RestClient.create();
//
//    public void saveDocument(String text) {
//        List<Float> vector = embeddingService.getEmbedding(text);
//
//        ChromaAddRequestDto request = ChromaAddRequestDto.builder()
//            .embeddings(List.of(vector))
//            .documents(List.of(text))
//            .ids(List.of(UUID.randomUUID().toString())) // 유니크 ID 생성
//            .build();
//
//        restClient.post()
//            .uri(chromaBaseUrl + "/collections/" + collectionId + "/add")
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(request)
//            .retrieve()
//            .toBodilessEntity();
//    }
//
//    public String searchRelatedContext(String question) {
//        String requestUrl = chromaBaseUrl + "/collections/" + collectionId + "/query"; // URL 미리 생성
//
//        try {
//            log.info("RAG 검색 시작: 질문='{}'", question);
//
//            // 1. 질문을 벡터화
//            List<Float> queryVector = embeddingService.getEmbedding(question);
//            if (queryVector.isEmpty()) {
//                log.warn("임베딩 생성 실패: 벡터가 비어있습니다.");
//                return "";
//            }
//
//            // 2. 검색 요청 DTO 생성
//            ChromaSearchRequestDto request = ChromaSearchRequestDto.builder()
//                .queryEmbeddings(List.of(queryVector))
//                .nResults(3)
//                .build();
//
//            log.info("ChromaDB 요청 URL: {}", requestUrl); // [중요] 실제 요청 URL 확인
//
//            // 3. ChromaDB로 POST 요청
//            ChromaSearchResponseDto response = restClient.post()
//                .uri(requestUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(request)
//                .retrieve()
//                .body(ChromaSearchResponseDto.class);
//
//            if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty() && !response.getDocuments().get(0).isEmpty()) {
//                log.info("RAG 검색 성공: 문서 {}개 발견", response.getDocuments().get(0).size());
//                return String.join("\n", response.getDocuments().get(0));
//            }
//
//        } catch (Exception e) {
//            // [중요] 에러가 나도 죽지 않고 로그만 남김
//            log.error("RAG 검색 중 오류 발생 (URL: {}): {}", requestUrl, e.getMessage());
//            // 필요하다면 e.printStackTrace()로 상세 로그 확인
//        }
//
//        log.info("RAG 검색 결과 없음 (또는 오류 발생)");
//        return ""; // 에러 발생 시 빈 문자열 반환 -> 일반 채팅으로 전환됨
//    }
//}
