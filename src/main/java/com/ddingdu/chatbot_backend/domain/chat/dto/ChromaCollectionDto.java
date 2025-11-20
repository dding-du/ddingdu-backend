package com.ddingdu.chatbot_backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 혹시 모를 다른 필드(metadata 등)는 무시
public class ChromaCollectionDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;
}
