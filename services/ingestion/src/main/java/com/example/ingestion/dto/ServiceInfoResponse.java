package com.example.ingestion.dto;

public record ServiceInfoResponse(
    String service,
    String description,
    String version,
    String api,
    String documentation
) {}
