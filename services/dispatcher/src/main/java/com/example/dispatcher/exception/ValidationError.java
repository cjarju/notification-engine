package com.example.dispatcher.exception;

public record ValidationError(
        String field,
        String message
) {}
