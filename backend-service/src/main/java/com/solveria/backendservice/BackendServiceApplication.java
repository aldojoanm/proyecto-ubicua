package com.solveria.backendservice;

import com.solveria.backendservice.config.security.JwtProperties;
import com.solveria.backendservice.travel.infrastructure.ai.AiServiceProperties;
import com.solveria.backendservice.travel.infrastructure.redis.RagCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    JwtProperties.class,
    AiServiceProperties.class,
    RagCacheProperties.class
})
public class BackendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendServiceApplication.class, args);
    }
}
