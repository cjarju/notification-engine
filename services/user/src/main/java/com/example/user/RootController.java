package com.example.user;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
            "service", "user-service",
            "version", "v1",
            "api", "/api/v1/users",
            "description", "User Management API",
            "documentation", "/swagger-ui.html"

        );
    }
}
