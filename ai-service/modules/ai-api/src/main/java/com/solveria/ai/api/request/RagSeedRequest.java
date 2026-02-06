package com.solveria.ai.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RagSeedRequest(
        @NotBlank
        @Size(max = 1000)
        String content,

        @NotBlank
        @Size(max = 120)
        String namespace
) {
}
