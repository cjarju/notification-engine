package com.example.account.preference.dto;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;

import jakarta.validation.constraints.NotNull;

public record UserPreferenceCreateRequest(
    @NotNull(message = "{user.preference.category.required}")
    AlertCategory category,

    @NotNull(message = "{user.preference.channel.required}")
    DeliveryChannel channel,

    Boolean enabled
) {}
