package com.example.dispatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.user")
public record UserServiceProperties(String url) {
}
