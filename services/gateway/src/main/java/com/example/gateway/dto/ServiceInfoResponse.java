package com.example.gateway.dto;

import java.util.List;

public record ServiceInfoResponse(
    String service,
    String description,
    String version,
    List<String> endpoints
) {}
