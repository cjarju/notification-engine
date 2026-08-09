package com.example.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.dispatcher")
public record DispatcherProperties(String url) {
}
