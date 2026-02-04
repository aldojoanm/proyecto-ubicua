package com.solveria.backendservice.travel.infrastructure.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;

@Component
public class RagCacheService {

    private final StringRedisTemplate redisTemplate;
    private final RagCacheProperties properties;

    public RagCacheService(StringRedisTemplate redisTemplate, RagCacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public Optional<String> getCachedAnswer(String tenantId, String destinationId, String question) {
        String key = buildKey(tenantId, destinationId, question);
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public void cacheAnswer(String tenantId, String destinationId, String question, String answer) {
        String key = buildKey(tenantId, destinationId, question);
        Duration ttl = Duration.ofSeconds(properties.ttlSeconds());
        redisTemplate.opsForValue().set(key, answer, ttl);
    }

    private String buildKey(String tenantId, String destinationId, String question) {
        return "rag:tenant:" + tenantId + ":dest:" + destinationId + ":q:" + sha256(question);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
