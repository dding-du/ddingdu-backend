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

    public void saveDocument(String text) {
        List<Float> vector = embeddingService.getEmbedding(text);

        ChromaAddRequestDto request = ChromaAddRequestDto.builder()
            .embeddings(List.of(vector))
            .documents(List.of(text))
            .ids(List.of(UUID.randomUUID().toString())) // 유니크 ID 생성
            .build();

        restClient.post()
            .uri(chromaBaseUrl + "/collections/" + collectionId + "/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }

    public String searchRelatedContext(String question) {
        List<Float> queryVector = embeddingService.getEmbedding(question);

        ChromaSearchRequestDto request = ChromaSearchRequestDto.builder()
            .queryEmbeddings(List.of(queryVector))
            .nResults(3)// 가장 유사한 3개 조회
            .build();

        ChromaSearchResponseDto response = restClient.post()
            .uri(chromaBaseUrl + "/collections/" + collectionId + "/query")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(ChromaSearchResponseDto.class);

        if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
            return String.join("\n", response.getDocuments().get(0));
        }
        return "";
    }
}
