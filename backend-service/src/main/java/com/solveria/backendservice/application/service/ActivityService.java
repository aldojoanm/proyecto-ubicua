package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Activity;
import com.solveria.backendservice.infrastructure.repository.ActivityRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Activity create(String title, String category, String location, LocalDateTime startsAt, LocalDateTime endsAt) {
        Activity activity = new Activity(title, category, location, startsAt, endsAt);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            activity.setTenantId(tenantId);
        }
        return repository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<Activity> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
