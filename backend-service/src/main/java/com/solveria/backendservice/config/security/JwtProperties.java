package com.solveria.backendservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT security.
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        boolean enabled,
        String tenantClaim
) {
}
