package com.solveria.backendservice.travel.infrastructure.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "destination_qa")
public class DestinationQaDocument {

    @Id
    private String id;

    private String tenantId;
    private String destinationId;
    private Long tripId;
    private String question;
    private String answer;
    private int promptTokens;
    private int completionTokens;
    private Instant askedAt;

    protected DestinationQaDocument() {
    }

    public DestinationQaDocument(String tenantId, String destinationId, Long tripId, String question, String answer, int promptTokens, int completionTokens, Instant askedAt) {
        this.tenantId = tenantId;
        this.destinationId = destinationId;
        this.tripId = tripId;
        this.question = question;
        this.answer = answer;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.askedAt = askedAt;
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public Long getTripId() {
        return tripId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public Instant getAskedAt() {
        return askedAt;
    }
}
