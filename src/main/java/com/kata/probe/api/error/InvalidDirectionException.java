
package com.kata.probe.api.error;

/**
 * Signals an invalid direction value in the API payload.
 * Handled globally to return HTTP 400 with a unified error shape.
 */
public class InvalidDirectionException extends RuntimeException {
    public InvalidDirectionException(String message) {
        super(message);
    }
}
