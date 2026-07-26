package com.example.account.common.dto;

public record ServiceInfoResponse(
    String service,
    String description,
    String version,
    String api,
    String documentation
) {}
