package com.ddingdu.chatbot_backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {

        // JWT 인증 방식 설정
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("Bearer Authentication");

        return new OpenAPI()
            .info(apiInfo())
            .servers(List.of(
                new Server()
                    .url(serverUrl)
                    .description("띵듀 백엔드 서버")
            ))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", securityScheme))
            .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
            .title("띵듀 (Ddingdu) API 문서")
            .description("명지대학교 띵듀로이드 챗봇 서비스 API 명세서")
            .version("v1.0.0");
    }
}