package com.example.dispatcher.dto;

import com.example.dispatcher.enums.AlertCategory;
import com.example.dispatcher.enums.DeliveryChannel;

public record UserPreference(
        Long id,
        String username,
        AlertCategory category,
        DeliveryChannel channel,
        boolean enabled
) {}
