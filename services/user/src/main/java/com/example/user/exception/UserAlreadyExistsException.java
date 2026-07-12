package com.example.user.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        this(email, null);
    }

    public UserAlreadyExistsException(String email, Throwable cause) {
        super("A user with email '%s' already exists.".formatted(email), cause);
    }
}
