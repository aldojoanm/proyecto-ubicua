package com.solveria.backendservice.travel.infrastructure.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.service.rag-cache")
public record RagCacheProperties(long ttlSeconds) {
}
