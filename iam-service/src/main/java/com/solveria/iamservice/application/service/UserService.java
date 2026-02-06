package com.solveria.iamservice.application.service;

import com.solveria.core.iam.application.port.UserRepositoryPort;
import com.solveria.core.iam.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String username, String email, String tenantId) {
        User user = new User(username, email, true);
        user.setTenantId(tenantId);
        return userRepository.save(user);
    }

    public List<User> list() {
        return userRepository.findAll();
    }
}
