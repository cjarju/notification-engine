package com.example.dispatcher.dto;

import com.example.dispatcher.enums.DispatchStatus;

import java.time.OffsetDateTime;

public record DispatchResponse(
        String trackingId,
        Long userId,
        DispatchStatus status,
        String message,
        OffsetDateTime timestamp
) {}
