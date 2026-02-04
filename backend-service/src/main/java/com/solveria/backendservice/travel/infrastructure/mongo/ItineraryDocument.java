package com.solveria.backendservice.travel.infrastructure.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "trip_itineraries")
public class ItineraryDocument {

    @Id
    private String id;

    private String tenantId;
    private Long tripId;
    private int version;
    private String content;
    private String model;
    private int tokensUsed;
    private Instant generatedAt;

    protected ItineraryDocument() {
    }

    public ItineraryDocument(String tenantId, Long tripId, int version, String content, String model, int tokensUsed, Instant generatedAt) {
        this.tenantId = tenantId;
        this.tripId = tripId;
        this.version = version;
        this.content = content;
        this.model = model;
        this.tokensUsed = tokensUsed;
        this.generatedAt = generatedAt;
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Long getTripId() {
        return tripId;
    }

    public int getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public String getModel() {
        return model;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
