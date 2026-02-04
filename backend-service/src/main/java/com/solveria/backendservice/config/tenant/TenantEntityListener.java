package com.solveria.backendservice.config.tenant;

import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.PrePersist;

public class TenantEntityListener {

    @PrePersist
    public void onPrePersist(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (baseEntity.getTenantId() == null || baseEntity.getTenantId().isBlank()) {
                String tenantId = SecurityTenantContext.getTenantId();
                if (tenantId != null && !tenantId.isBlank()) {
                    baseEntity.setTenantId(tenantId);
                }
            }
        }
    }
}
