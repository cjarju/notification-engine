package com.example.user;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.ServiceInfoResponse;
import com.example.user.constants.ApiPaths;
import com.example.user.constants.ServiceInfo;

@RestController
public class RootController {

    @GetMapping("/")
    public ServiceInfoResponse root() {
        return new ServiceInfoResponse(
            ServiceInfo.NAME,
            ServiceInfo.DESCRIPTION,
            ServiceInfo.VERSION,
            ApiPaths.USERS,
            ApiPaths.SWAGGER_UI
        );
    }
}
