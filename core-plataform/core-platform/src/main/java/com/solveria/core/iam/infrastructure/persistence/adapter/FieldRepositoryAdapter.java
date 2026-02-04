package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.FieldRepositoryPort;
import com.solveria.core.iam.domain.model.Field;
import com.solveria.core.iam.infrastructure.persistence.repository.FieldJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class FieldRepositoryAdapter implements FieldRepositoryPort {

    private final FieldJpaRepository fieldJpaRepository;

    public FieldRepositoryAdapter(FieldJpaRepository fieldJpaRepository) {
        this.fieldJpaRepository = fieldJpaRepository;
    }

    @Override
    public Optional<Field> findById(Long id) {
        return fieldJpaRepository.findById(id);
    }


}
