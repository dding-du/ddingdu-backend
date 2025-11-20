package com.ddingdu.chatbot_backend.domain.chat.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
class WebClientConfig {

    @Value("${api.fastapi.connect-timeout:5000}")
    private int connectTimeout;

    // 5분 (읽기 타임아웃: AI 응답 대기 시간)
    @Value("${api.fastapi.read-timeout:300000}")
    private int readTimeout;

    @Value("${api.fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public WebClient fastApiWebClient() {
        // 1. Reactor HttpClient 설정 (Netty 기반 논블로킹 타임아웃 설정)
        HttpClient httpClient = HttpClient.create()
            // 연결 타임아웃
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .responseTimeout(Duration.ofMillis(readTimeout))
            .doOnConnected(conn ->
                conn
                    // 읽기 타임아웃 핸들러 추가
                    .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    // 쓰기 타임아웃 핸들러 추가
                    .addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
            );

        // 2. WebClient 생성 및 설정
        return WebClient.builder()
            .baseUrl(fastApiBaseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}