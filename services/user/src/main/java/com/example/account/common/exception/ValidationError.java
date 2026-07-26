package com.example.account.common.exception;

public record ValidationError(
        String field,
        String message
) {}
