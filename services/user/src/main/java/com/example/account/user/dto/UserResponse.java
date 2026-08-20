package com.example.account.user.dto;

import java.time.Instant;

public record UserResponse(
    Long id,
    String username,
    String email,
    String phoneNumber,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
