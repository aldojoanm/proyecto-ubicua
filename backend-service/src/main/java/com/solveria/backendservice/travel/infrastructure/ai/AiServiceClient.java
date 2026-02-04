package com.solveria.backendservice.travel.infrastructure.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public CompleteResult complete(String prompt) {
        CompleteRequest request = new CompleteRequest(prompt);
        return restClient.post()
                .uri("/api/v1/ai/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CompleteResult.class);
    }

    public RagQaResult ragQa(String question, String namespace) {
        RagQaRequest request = new RagQaRequest(question, namespace);
        return restClient.post()
                .uri("/api/v1/ai/rag/qa")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RagQaResult.class);
    }

    public record CompleteRequest(String prompt) {
    }

    public record CompleteResult(String content, String model, int tokensUsed) {
    }

    public record RagQaRequest(String question, String namespace) {
    }

    public record RagQaResult(String answer, int promptTokens, int completionTokens) {
    }
}
