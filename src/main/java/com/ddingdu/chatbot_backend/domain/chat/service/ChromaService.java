package com.ddingdu.chatbot_backend.domain.chat.service;

import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaAddRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchRequestDto;
import com.ddingdu.chatbot_backend.domain.chat.dto.ChromaSearchResponseDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ChromaService {

    private final GeminiEmbeddingService embeddingService;

    @Value("${chroma.url}")
    private String chromaBaseUrl;

    @Value("${chroma.collection-id}")
    private String collectionId;

    private final RestClient restClient = RestClient.create();

    // 1. 데이터 저장 (임베딩 -> ChromaDB 전송)
    public void saveDocument(String text) {
        // 1-1. 텍스트를 벡터로 변환
        List<Float> vector = embeddingService.getEmbedding(text);

        // 1-2. 요청 바디 생성
        ChromaAddRequestDto request = ChromaAddRequestDto.builder()
            .embeddings(List.of(vector))
            .documents(List.of(text))
            .ids(List.of(UUID.randomUUID().toString())) // 유니크 ID 생성
            .build();

        // 1-3. ChromaDB로 POST 요청
        restClient.post()
            .uri(chromaBaseUrl + "/collections/" + collectionId + "/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }

    // 2. 검색 (질문 -> 임베딩 -> ChromaDB 검색)
    public String searchRelatedContext(String question) {
        // 2-1. 질문을 벡터로 변환
        List<Float> queryVector = embeddingService.getEmbedding(question);

        // 2-2. 검색 요청 바디 생성
        ChromaSearchRequestDto request = ChromaSearchRequestDto.builder()
            .queryEmbeddings(List.of(queryVector))
            .nResults(3)// 가장 유사한 3개 조회
            .build();

        // 2-3. ChromaDB로 POST 요청 (Query)
        ChromaSearchResponseDto response = restClient.post()
            .uri(chromaBaseUrl + "/collections/" + collectionId + "/query")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(ChromaSearchResponseDto.class);

        // 2-4. 결과 텍스트 합치기
        if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
            return String.join("\n", response.getDocuments().get(0));
        }
        return "";
    }
}
