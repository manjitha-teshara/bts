package org.bts.app.dto;

/**
 * Data Transfer Object representing an API error response.
 *
 * @param error A descriptive error message.
 * @param status The HTTP status code of the error.
 */
public record ErrorResponseDTO(
        String error,
        int status
) {}
