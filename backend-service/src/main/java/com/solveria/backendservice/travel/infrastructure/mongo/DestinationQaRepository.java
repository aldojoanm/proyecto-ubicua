package com.solveria.backendservice.travel.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DestinationQaRepository extends MongoRepository<DestinationQaDocument, String> {
}
