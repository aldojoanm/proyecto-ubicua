package com.solveria.backendservice.travel.application;

import com.solveria.backendservice.travel.infrastructure.ai.AiServiceClient;
import com.solveria.backendservice.travel.infrastructure.mongo.DestinationQaDocument;
import com.solveria.backendservice.travel.infrastructure.mongo.DestinationQaRepository;
import com.solveria.backendservice.travel.infrastructure.redis.RagCacheService;
import com.solveria.core.security.context.SecurityTenantContext;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DestinationQaService {

    private final AiServiceClient aiServiceClient;
    private final DestinationQaRepository repository;
    private final RagCacheService cacheService;

    public DestinationQaService(AiServiceClient aiServiceClient, DestinationQaRepository repository, RagCacheService cacheService) {
        this.aiServiceClient = aiServiceClient;
        this.repository = repository;
        this.cacheService = cacheService;
    }

    public DestinationQaResult ask(String destinationId, String question, Long tripId) {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }

        String cached = cacheService.getCachedAnswer(tenantId, destinationId, question).orElse(null);
        if (cached != null) {
            return new DestinationQaResult(cached, true, 0, 0);
        }

        String namespace = buildNamespace(tenantId, destinationId, tripId);
        AiServiceClient.RagQaResult result = aiServiceClient.ragQa(question, namespace);
        if (result == null) {
            return null;
        }

        DestinationQaDocument doc = new DestinationQaDocument(
                tenantId,
                destinationId,
                tripId,
                question,
                result.answer(),
                result.promptTokens(),
                result.completionTokens(),
                Instant.now()
        );
        repository.save(doc);
        cacheService.cacheAnswer(tenantId, destinationId, question, result.answer());

        return new DestinationQaResult(result.answer(), false, result.promptTokens(), result.completionTokens());
    }

    private String buildNamespace(String tenantId, String destinationId, Long tripId) {
        if (tripId != null) {
            return "tenant:" + tenantId + ":trip:" + tripId + ":destination:" + destinationId;
        }
        return "tenant:" + tenantId + ":destination:" + destinationId;
    }

    public record DestinationQaResult(String answer, boolean cached, int promptTokens, int completionTokens) {
    }
}
