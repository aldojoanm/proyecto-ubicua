package com.solveria.backendservice.api.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        String errorCode,
        String message,
        List<FieldError> fieldErrors,
        Instant timestamp
) {
    public record FieldError(String field, String message) {
    }
}
