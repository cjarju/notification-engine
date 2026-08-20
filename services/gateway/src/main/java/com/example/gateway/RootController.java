package com.example.gateway;

import com.example.gateway.constants.ApiPaths;
import com.example.gateway.constants.ServiceInfo;
import com.example.gateway.dto.ServiceInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
@RestController
@RequestMapping(ApiPaths.PUBLIC_ROOT)
public class RootController {

    @GetMapping
    public ServiceInfoResponse getInfo() {
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
