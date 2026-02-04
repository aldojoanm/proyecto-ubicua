package com.solveria.backendservice.config.tenant;

import com.solveria.core.security.context.SecurityTenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtTenantContextFilter extends OncePerRequestFilter {

    private final String tenantClaim;
    private final List<RequestMatcher> excludedMatchers;

    @PersistenceContext
    private EntityManager entityManager;

    public JwtTenantContextFilter(String tenantClaim) {
        this.tenantClaim = tenantClaim;
        this.excludedMatchers = List.of(
                new AntPathRequestMatcher("/actuator/health/**"),
                new AntPathRequestMatcher("/actuator/info/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/error")
        );
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        boolean tenantFilterEnabled = false;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String tenantId = jwtAuth.getToken().getClaimAsString(tenantClaim);
                if (tenantId == null || tenantId.isBlank()) {
                    writeMissingTenant(response);
                    return;
                }
                SecurityTenantContext.setTenantId(tenantId);
                tenantFilterEnabled = enableTenantFilter(tenantId);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (tenantFilterEnabled) {
                disableTenantFilter();
            }
            SecurityTenantContext.clear();
        }
    }

    private boolean enableTenantFilter(String tenantId) {
        if (entityManager == null) {
            return false;
        }
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("tenantFilter");
        filter.setParameter("tenantId", tenantId);
        return true;
    }

    private void disableTenantFilter() {
        if (entityManager == null) {
            return;
        }
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("tenantFilter");
    }

    private void writeMissingTenant(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"error\":\"TENANT_MISSING\",\"message\":\"Missing tenant claim: " + tenantClaim + "\"}"
        );
    }
}
