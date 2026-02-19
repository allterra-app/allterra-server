package com.allterra.server.exception;

/**
 * Exception when user email is not unique.
 */
public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(final String email) {
        super(String.format("User with email [%s] already exists", email));
    }
}
