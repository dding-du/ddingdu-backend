package com.ddingdu.chatbot_backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChromaAddRequestDto {

    @JsonProperty("embeddings")
    private List<List<Float>> embeddings;

    @JsonProperty("documents")
    private List<String> documents;

    @JsonProperty("ids")
    private List<String> ids;
}