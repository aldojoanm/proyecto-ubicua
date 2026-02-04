package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.RoleRepositoryPort;
import com.solveria.core.iam.domain.model.Role;
import com.solveria.core.iam.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Role save(Role role) {
        return roleJpaRepository.save(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id);
    }

    @Override
    public Set<Role> findByIds(Set<Long> ids) {
        return Set.copyOf(roleJpaRepository.findAllById(ids));
    }
}
