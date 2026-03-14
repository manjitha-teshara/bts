package org.bts.app.exception;

/**
 * Custom exception thrown when the incoming request data is invalid or missing required fields.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
