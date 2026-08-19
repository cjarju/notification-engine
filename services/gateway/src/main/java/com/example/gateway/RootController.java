package com.example.gateway;

import com.example.gateway.constants.ApiPaths;
import com.example.gateway.constants.ServiceInfo;
import com.example.gateway.dto.ServiceInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class RootController {

    @GetMapping(ApiPaths.PUBLIC_ROOT)
    public ServiceInfoResponse root() {
        return new ServiceInfoResponse(
            ServiceInfo.NAME,
            ServiceInfo.DESCRIPTION,
            ServiceInfo.VERSION,
            List.of(
                ApiPaths.NOTIFICATIONS,
                ApiPaths.USERS
            )
        );
    }
}
