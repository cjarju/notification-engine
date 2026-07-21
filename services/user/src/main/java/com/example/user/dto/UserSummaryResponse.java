package com.example.user.dto;

public record UserSummaryResponse(
    Long id,
    String username,
    String email,
    Boolean active
) {}
