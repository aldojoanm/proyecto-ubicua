package com.solveria.backendservice.travel.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItineraryRepository extends MongoRepository<ItineraryDocument, String> {

    Optional<ItineraryDocument> findTopByTenantIdAndTripIdOrderByVersionDesc(String tenantId, Long tripId);
}
