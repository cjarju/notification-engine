package com.example.ingestion.exception;

public record ValidationError(
        String field,
        String message
) {}
