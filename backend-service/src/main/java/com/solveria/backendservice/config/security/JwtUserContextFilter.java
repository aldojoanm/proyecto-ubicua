package com.solveria.backendservice.config.security;

import com.solveria.core.security.context.SecurityUserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtUserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String subject = jwtAuth.getToken().getSubject();
                if (subject != null) {
                    try {
                        SecurityUserContext.setUserId(Long.parseLong(subject));
                    } catch (NumberFormatException ignored) {
                        // If subject is not numeric, leave unset
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            SecurityUserContext.clear();
        }
    }
}
