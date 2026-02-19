package com.allterra.server.authentication.exception;

/**
 * Exception for invalid refresh tokens.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(final String message) {
        super(message);
    }
}

