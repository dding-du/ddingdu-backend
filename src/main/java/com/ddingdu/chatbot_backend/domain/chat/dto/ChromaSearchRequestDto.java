package com.ddingdu.chatbot_backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChromaSearchRequestDto {

    // ChromaDB 필드명: query_embeddings
    @JsonProperty("query_embeddings")
    private List<List<Float>> queryEmbeddings;

    // ChromaDB 필드명: n_results
    @JsonProperty("n_results")
    private int nResults;
}