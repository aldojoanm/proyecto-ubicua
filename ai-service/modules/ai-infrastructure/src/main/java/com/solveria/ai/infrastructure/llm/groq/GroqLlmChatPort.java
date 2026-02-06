package com.solveria.ai.infrastructure.llm.groq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solveria.ai.application.dto.ChatResultDto;
import com.solveria.ai.application.port.out.LlmChatPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

public class GroqLlmChatPort implements LlmChatPort {

    private final RestClient restClient;
    private final GroqProperties properties;

    public GroqLlmChatPort(RestClient restClient, GroqProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public ChatResultDto chat(String prompt) {
        GroqChatRequest request = new GroqChatRequest(
                properties.chatModel(),
                List.of(new GroqMessage("user", prompt)),
                0.2
        );

        GroqChatResponse response = restClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GroqChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return new ChatResultDto("", 0, 0);
        }

        String answer = response.choices().get(0).message() != null
                ? response.choices().get(0).message().content()
                : "";
        int promptTokens = response.usage() != null && response.usage().promptTokens() != null
                ? response.usage().promptTokens()
                : 0;
        int completionTokens = response.usage() != null && response.usage().completionTokens() != null
                ? response.usage().completionTokens()
                : 0;

        return new ChatResultDto(answer, promptTokens, completionTokens);
    }

    public record GroqChatRequest(
            String model,
            List<GroqMessage> messages,
            double temperature
    ) {
    }

    public record GroqMessage(
            String role,
            String content
    ) {
    }

    public record GroqChatResponse(
            List<GroqChoice> choices,
            GroqUsage usage
    ) {
    }

    public record GroqChoice(GroqMessage message) {
    }

    public record GroqUsage(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("completion_tokens") Integer completionTokens
    ) {
    }
}
