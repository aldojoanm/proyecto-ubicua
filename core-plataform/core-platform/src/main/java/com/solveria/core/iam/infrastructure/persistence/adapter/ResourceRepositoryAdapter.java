package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ResourceRepositoryPort;
import com.solveria.core.iam.domain.model.Resource;
import com.solveria.core.iam.infrastructure.persistence.repository.ResourceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class ResourceRepositoryAdapter implements ResourceRepositoryPort {

    private final ResourceJpaRepository resourceJpaRepository;

    public ResourceRepositoryAdapter(ResourceJpaRepository resourceJpaRepository) {
        this.resourceJpaRepository = resourceJpaRepository;
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceJpaRepository.findById(id);
    }
}
