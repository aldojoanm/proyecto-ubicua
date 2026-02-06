package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Notification;
import com.solveria.backendservice.domain.model.NotificationChannel;
import com.solveria.backendservice.infrastructure.repository.NotificationRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Notification create(NotificationChannel channel, String recipient, String message) {
        Notification notification = new Notification(channel, recipient, message);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            notification.setTenantId(tenantId);
        }
        return repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
