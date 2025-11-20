package com.ddingdu.chatbot_backend.domain.chat.service;

import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaAddRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaCollectionDto;
import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchResponseDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChromaService {

    private final GeminiEmbeddingService embeddingService;

    @Value("${chroma.url}")
    private String chromaBaseUrl;

    @Value("${chroma.collection-id}")
    private String collectionName;

    private final RestClient restClient = RestClient.create();

    private String cachedCollectionId = null;

    private String getCollectionId() {
        if (cachedCollectionId != null) {
            return cachedCollectionId;
        }

        try {
            // ChromaDB의 모든 컬렉션 목록 조회 (GET /api/v1/collections)
            // (ChromaDB API 스펙상 이름으로 바로 ID를 주는 API가 명확지 않아 목록에서 찾습니다)
            List<ChromaCollectionDto> collections = restClient.get()
                .uri(chromaBaseUrl + "/collections")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ChromaCollectionDto>>() {});

            if (collections != null) {
                for (ChromaCollectionDto col : collections) {
                    if (col.getName().equals(collectionName)) {
                        log.info("ChromaDB 컬렉션 ID 발견: {} -> {}", collectionName, col.getId());
                        cachedCollectionId = col.getId();
                        return col.getId();
                    }
                }
            }
        } catch (Exception e) {
            log.error("ChromaDB 컬렉션 목록 조회 실패: {}", e.getMessage());
        }

        log.warn("컬렉션 이름 '{}'을 찾을 수 없습니다.", collectionName);
        return null;
    }

    public void saveDocument(String text) {
        List<Float> vector = embeddingService.getEmbedding(text);

        ChromaAddRequestDto request = ChromaAddRequestDto.builder()
            .embeddings(List.of(vector))
            .documents(List.of(text))
            .ids(List.of(UUID.randomUUID().toString())) // 유니크 ID 생성
            .build();

        restClient.post()
            .uri(chromaBaseUrl + "/collections/" + collectionName + "/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }

    public String searchRelatedContext(String question) {

        String realId = getCollectionId();

        if (realId == null) {
            log.warn("RAG 검색 불가: 컬렉션 ID를 찾지 못했습니다.");
            return "";
        }

        String requestUrl = chromaBaseUrl + "/collections/" + realId + "/query"; // URL 미리 생성

        try {
            log.info("RAG 검색 시작: 질문='{}'", question);

            // 1. 질문을 벡터화
            List<Float> queryVector = embeddingService.getEmbedding(question);
            if (queryVector.isEmpty()) {
                log.warn("임베딩 생성 실패: 벡터가 비어있습니다.");
                return "";
            }

            // 2. 검색 요청 DTO 생성
            ChromaSearchRequestDto request = ChromaSearchRequestDto.builder()
                .queryEmbeddings(List.of(queryVector))
                .nResults(3)
                .build();

            log.info("ChromaDB 요청 URL: {}", requestUrl); // [중요] 실제 요청 URL 확인

            // 3. ChromaDB로 POST 요청
            ChromaSearchResponseDto response = restClient.post()
                .uri(requestUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChromaSearchResponseDto.class);

            if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty() && !response.getDocuments().get(0).isEmpty()) {
                log.info("RAG 검색 성공: 문서 {}개 발견", response.getDocuments().get(0).size());
                return String.join("\n", response.getDocuments().get(0));
            }

        } catch (Exception e) {
            // [중요] 에러가 나도 죽지 않고 로그만 남김
            log.error("RAG 검색 중 오류 발생 (URL: {}): {}", requestUrl, e.getMessage());
            // 필요하다면 e.printStackTrace()로 상세 로그 확인
        }
        log.info("RAG 검색 결과 없음 (또는 오류 발생)");
        return ""; // 에러 발생 시 빈 문자열 반환 -> 일반 채팅으로 전환됨
    }
}
