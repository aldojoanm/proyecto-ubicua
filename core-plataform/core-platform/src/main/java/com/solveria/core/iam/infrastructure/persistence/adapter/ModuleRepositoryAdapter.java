package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ModuleRepositoryPort;
import com.solveria.core.iam.domain.model.Module;
import com.solveria.core.iam.infrastructure.persistence.repository.ModuleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository moduleJpaRepository;

    public ModuleRepositoryAdapter(ModuleJpaRepository moduleJpaRepository) {
        this.moduleJpaRepository = moduleJpaRepository;
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleJpaRepository.findById(id);
    }


}
