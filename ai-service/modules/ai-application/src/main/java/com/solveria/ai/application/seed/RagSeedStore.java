package com.solveria.ai.application.seed;

import com.solveria.ai.application.dto.RagChunkDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RagSeedStore {

    private static final Map<String, List<RagChunkDto>> SEEDED = new ConcurrentHashMap<>();

    private RagSeedStore() {
    }

    public static void seed(String namespace, String content) {
        SEEDED.put(namespace, List.of(new RagChunkDto(content)));
    }

    public static List<RagChunkDto> get(String namespace, int topK) {
        List<RagChunkDto> chunks = SEEDED.get(namespace);
        if (chunks == null || chunks.isEmpty()) {
            return List.of();
        }
        return chunks.subList(0, Math.min(topK, chunks.size()));
    }
}
