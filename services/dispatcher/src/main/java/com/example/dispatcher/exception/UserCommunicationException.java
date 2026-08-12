package com.example.dispatcher.exception;

public class UserCommunicationException extends RuntimeException {

    public UserCommunicationException(String message) {
        this(message, null);
    }

    public UserCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
