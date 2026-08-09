package com.example.ingestion.dto;

import java.time.OffsetDateTime;

public record IngestResponse(
    String trackingId,
    Long userId,
    String status,
    OffsetDateTime timestamp
) {}
