package com.solveria.core.iam.application.port;

import com.solveria.core.iam.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
}
