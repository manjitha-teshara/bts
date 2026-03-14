package org.bts.app.exception;

/**
 * Custom exception thrown when a booking fails due to insufficient seat availability
 * for the requested route and passenger count.
 */
public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {
        super(message);
    }
}
