package org.bts.app.exception;

/**
 * Custom exception thrown when a requested route (origin to destination)
 * does not exist in the system.
 */
public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message) {
        super(message);
    }
}
