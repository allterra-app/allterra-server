package com.allterra.server.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Unified API error response model.
 *
 * @param status textual status
 * @param code HTTP status code
 * @param error HTTP status reason phrase
 * @param message human-readable error message
 * @param path request path
 * @param timestamp response timestamp
 * @param validationErrors field-level validation errors
 */
public record ApiErrorResponse(
        String status,
        int code,
        String error,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> validationErrors
) {

    /**
     * Creates error response without validation errors.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @return error response
     */
    public static ApiErrorResponse of(final HttpStatus status, final String message, final String path) {
        return of(status, message, path, Map.of());
    }

    /**
     * Creates error response with validation errors.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @param validationErrors validation errors map
     * @return error response
     */
    public static ApiErrorResponse of(
            final HttpStatus status,
            final String message,
            final String path,
            final Map<String, String> validationErrors
    ) {
        return new ApiErrorResponse(
                "error",
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                Instant.now(),
                validationErrors == null ? Map.of() : validationErrors
        );
    }
}

