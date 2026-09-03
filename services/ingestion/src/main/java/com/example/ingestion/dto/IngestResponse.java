package com.example.ingestion.dto;

import java.time.Instant;

public record IngestResponse(
    String trackingId,
    Long userId,
    String status,
    Instant timestamp
) {}
