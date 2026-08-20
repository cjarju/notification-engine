package com.example.account.user.dto;

import java.time.OffsetDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String phoneNumber,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
