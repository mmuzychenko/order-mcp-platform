package com.example.orders.contracts.error;

import java.util.List;

/** Business error envelope. Never carries stack traces or sensitive records. */
public record ErrorEnvelope(
        ErrorCode code,
        String message,
        String correlationId,
        List<FieldError> fieldErrors,
        boolean retryable) {

    public record FieldError(String field, String message) {}
}
