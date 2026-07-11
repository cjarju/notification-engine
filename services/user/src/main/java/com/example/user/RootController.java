package com.example.user;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.ServiceInfoResponse;

@RestController
public class RootController {

    @GetMapping("/")
    public ServiceInfoResponse root() {
        return new ServiceInfoResponse(
            "user-service",
            "User Management API",
            "v1",
            "/api/v1/users",
            "/swagger-ui.html"
        );
    }
}
