package com.example.account.preference.dto;

import java.time.Instant;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;

public record UserPreferenceResponse(
    Long id,
    Long userId,
    String username,
    AlertCategory category,
    DeliveryChannel channel,
    boolean enabled,
    Instant createdAt,
    Instant updatedAt
) {}
