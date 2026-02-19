package com.allterra.server.authentication.exception;

/**
 * Exception for invalid login credentials.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(final String message) {
        super(message);
    }
}

