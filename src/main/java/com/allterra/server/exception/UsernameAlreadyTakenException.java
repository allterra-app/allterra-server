package com.allterra.server.exception;

/**
 * Exception when username is not unique.
 */
public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(final String username) {
        super(String.format("User with username [%s] already exists", username));
    }
}
