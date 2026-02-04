package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.ActionRepositoryPort;
import com.solveria.core.iam.domain.model.Action;
import com.solveria.core.iam.infrastructure.persistence.repository.ActionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class ActionRepositoryAdapter implements ActionRepositoryPort {

    private final ActionJpaRepository actionJpaRepository;

    public ActionRepositoryAdapter(ActionJpaRepository actionJpaRepository) {
        this.actionJpaRepository = actionJpaRepository;
    }

    @Override
    public Optional<Action> findById(Long id) {
        return actionJpaRepository.findById(id);
    }


}
