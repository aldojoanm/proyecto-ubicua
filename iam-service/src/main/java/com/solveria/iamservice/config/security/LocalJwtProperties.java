package com.solveria.iamservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.local")
public record LocalJwtProperties(
        boolean enabled,
        String issuer,
        String keyId,
        long accessTokenMinutes
) {
}
