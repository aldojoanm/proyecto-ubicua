package com.solveria.ai.infrastructure.llm.groq;

import com.solveria.ai.application.port.out.LlmChatPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(GroqProperties.class)
@ConditionalOnProperty(prefix = "groq", name = "api-key")
public class GroqLlmChatConfig {

    @Bean
    public RestClient groqRestClient(GroqProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.apiKey())
                .build();
    }

    @Bean
    public LlmChatPort groqLlmChatPort(RestClient groqRestClient, GroqProperties properties) {
        return new GroqLlmChatPort(groqRestClient, properties);
    }
}
