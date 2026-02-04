package com.solveria.backendservice.travel.application;

import com.solveria.backendservice.travel.domain.model.Trip;
import com.solveria.backendservice.travel.infrastructure.ai.AiServiceClient;
import com.solveria.backendservice.travel.infrastructure.mongo.ItineraryDocument;
import com.solveria.backendservice.travel.infrastructure.mongo.ItineraryRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ItineraryService {

    private final TripService tripService;
    private final ItineraryRepository itineraryRepository;
    private final AiServiceClient aiServiceClient;

    public ItineraryService(TripService tripService, ItineraryRepository itineraryRepository, AiServiceClient aiServiceClient) {
        this.tripService = tripService;
        this.itineraryRepository = itineraryRepository;
        this.aiServiceClient = aiServiceClient;
    }

    @Transactional
    public ItineraryDocument generate(Long tripId, String preferences) {
        Trip trip = tripService.getForTenant(tripId);
        if (trip == null) {
            return null;
        }

        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }

        String prompt = buildPrompt(trip, preferences);
        AiServiceClient.CompleteResult result = aiServiceClient.complete(prompt);
        if (result == null) {
            return null;
        }

        int version = itineraryRepository.findTopByTenantIdAndTripIdOrderByVersionDesc(tenantId, tripId)
                .map(doc -> doc.getVersion() + 1)
                .orElse(1);

        ItineraryDocument doc = new ItineraryDocument(
                tenantId,
                tripId,
                version,
                result.content(),
                result.model(),
                result.tokensUsed(),
                Instant.now()
        );
        trip.markItineraryReady();
        return itineraryRepository.save(doc);
    }

    @Transactional(readOnly = true)
    public ItineraryDocument getLatest(Long tripId) {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }
        return itineraryRepository.findTopByTenantIdAndTripIdOrderByVersionDesc(tenantId, tripId).orElse(null);
    }

    private String buildPrompt(Trip trip, String preferences) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a travel itinerary for a trip.")
          .append(" Destination: ").append(trip.getDestination()).append(".")
          .append(" Origin: ").append(trip.getOrigin()).append(".")
          .append(" Dates: ").append(trip.getStartDate()).append(" to ").append(trip.getEndDate()).append(".")
          .append(" Travelers: ").append(trip.getTravelersCount()).append(".");
        if (preferences != null && !preferences.isBlank()) {
            sb.append(" Preferences: ").append(preferences).append(".");
        }
        sb.append(" Provide a day-by-day plan with activities and suggestions.");
        return sb.toString();
    }
}
