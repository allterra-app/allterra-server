package com.allterra.server.exception;

/**
 * Exception for missing resources.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String message) {
        super(message);
    }
}

