package com.solveria.iamservice.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerUiWhitelistConfig {

    @Configuration
    static class SwaggerChainConfig {

        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
            http.securityMatcher(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-ui/**/*swagger-initializer.js"
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.GET,
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**/*swagger-initializer.js"
                            ).permitAll()
                            .anyRequest().permitAll()
                    )
                    .csrf(csrf -> csrf.disable());

            return http.build();
        }
    }
}
