package com.example.ingestion.exception;

public class DispatcherCommunicationException extends RuntimeException {

    public DispatcherCommunicationException(String message) {
        this(message, null);
    }
    
    public DispatcherCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
