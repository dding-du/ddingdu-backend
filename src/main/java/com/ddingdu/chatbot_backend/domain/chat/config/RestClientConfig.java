package com.ddingdu.chatbot_backend.domain.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000); // 5초
        factory.setReadTimeout(300000); // 5분

        return factory;
    }

    @Bean
    public RestClient fastApiRestClient(
        SimpleClientHttpRequestFactory factory) { // 3. 생성된 팩토리를 주입받음
        return RestClient.builder()
            .baseUrl(fastApiBaseUrl)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .requestFactory(factory)
            .build();
    }
}
