package com.solveria.core.audit.application;

import com.solveria.core.audit.application.port.out.AuditLogQueryPort;
import com.solveria.core.audit.domain.model.AuditLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Servicio de consulta de auditoría funcional.
 *
 * Application Layer:
 * - No contiene lógica de negocio compleja
 * - Orquesta consultas
 * - No conoce infraestructura; usa AuditLogQueryPort (port out).
 */
@Service
public class AuditQueryService {

    private final AuditLogQueryPort queryPort;

    public AuditQueryService(AuditLogQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    public List<AuditLog> findByTenant(String tenantId) {
        return queryPort.findByTenantId(tenantId);
    }

    public List<AuditLog> findByPeriod(Instant from, Instant to) {
        return queryPort.findByOccurredAtBetween(from, to);
    }
}
