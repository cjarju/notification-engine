package com.example.gateway.exception;

import org.springframework.http.HttpStatus;

public record GatewayError (
    HttpStatus status,
    String type,
    String title,
    String detail
) {}
