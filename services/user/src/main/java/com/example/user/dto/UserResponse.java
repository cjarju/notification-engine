package com.example.user.dto;

import java.time.OffsetDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String phoneNumber,
    Boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
