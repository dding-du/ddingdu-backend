package com.ddingdu.chatbot_backend.domain.chat.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import com.google.genai.types.Part;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiEmbeddingService {

    private final Client geminiClient;

    private static final String EMBEDDING_MODEL = "models/text-embedding-004";

    public List<Float> getEmbedding(String text) {
        try {
            // [변경점] Content 객체를 만들지 않고, text(String)를 바로 넘깁니다.
            // 파라미터 순서: (모델명, 텍스트, 설정)
            EmbedContentResponse response = geminiClient.models.embedContent(
                EMBEDDING_MODEL,
                text,
                (EmbedContentConfig) null // 설정이 없다면 null (형변환 명시)
            );

            // 결과 추출 로직 (AutoValue 패턴 대응)
            Optional<List<ContentEmbedding>> embeddingOpt = response.embeddings();

            if (embeddingOpt.isPresent() && !embeddingOpt.get().isEmpty()) {
                ContentEmbedding firstEmbedding = embeddingOpt.get().get(0);

                // [수정] values()가 Optional을 반환하므로, 한 번 더 벗겨냅니다.
                return firstEmbedding.values()
                    .orElse(Collections.emptyList());
            }

            return Collections.emptyList();

        } catch (Exception e) {
            log.error("임베딩 생성 실패: {}", e.getMessage());
            throw new RuntimeException("Gemini Embedding API 호출 실패", e);
        }
    }

}
