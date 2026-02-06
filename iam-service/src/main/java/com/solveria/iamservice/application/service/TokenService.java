package com.solveria.iamservice.application.service;

import com.solveria.iamservice.config.security.LocalJwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@ConditionalOnProperty(name = "security.jwt.local.enabled", havingValue = "true")
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final LocalJwtProperties properties;

    public TokenService(JwtEncoder jwtEncoder, LocalJwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public TokenResult issueToken(Long userId, String username, String tenantId, List<String> scopes) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(userId))
                .claim("preferred_username", username)
                .claim("tenant_id", tenantId)
                .claim("scope", String.join(" ", scopes))
                .build();

        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new TokenResult(tokenValue, "Bearer", expiresAt, scopes);
    }

    public record TokenResult(String accessToken, String tokenType, Instant expiresAt, List<String> scopes) {
    }
}
