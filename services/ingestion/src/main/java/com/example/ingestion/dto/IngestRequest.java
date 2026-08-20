package com.example.ingestion.dto;

import com.example.ingestion.enums.AlertCategory;
import com.example.ingestion.enums.DeliveryChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record IngestRequest(
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
