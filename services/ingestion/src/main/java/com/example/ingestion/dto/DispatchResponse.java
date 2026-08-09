package com.example.ingestion.dto;

public record DispatchResponse(
    String trackingId,
    String status,
    String details
) {}
