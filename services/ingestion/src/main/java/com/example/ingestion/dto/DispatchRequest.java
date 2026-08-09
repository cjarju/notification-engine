package com.example.ingestion.dto;

import com.example.ingestion.enums.AlertCategory;
import com.example.ingestion.enums.DeliveryChannel;

import java.util.Map;

public record DispatchRequest(
    String trackingId,
    Long userId,
    AlertCategory category,
    DeliveryChannel channel,
    String content,
    Map<String, Object> metadata
) {}
