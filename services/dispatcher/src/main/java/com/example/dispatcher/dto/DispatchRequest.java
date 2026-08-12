package com.example.dispatcher.dto;

import com.example.dispatcher.enums.AlertCategory;
import com.example.dispatcher.enums.DeliveryChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record DispatchRequest(
        @NotBlank(message = "Tracking ID is required")
        String trackingId,

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Category is required")
        AlertCategory category,

        @NotNull(message = "Channel is required")
        DeliveryChannel channel,

        @NotBlank(message = "Message content is required")
        String content,

        Map<String, Object> metadata
) {}
