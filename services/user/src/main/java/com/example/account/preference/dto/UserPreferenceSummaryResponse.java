package com.example.account.preference.dto;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;

public record UserPreferenceSummaryResponse(
    Long id,
    String username,
    AlertCategory category,
    DeliveryChannel channel,
    Boolean enabled
) {}
