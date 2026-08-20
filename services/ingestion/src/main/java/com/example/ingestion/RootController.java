package com.example.ingestion;

import com.example.ingestion.constants.ApiPaths;
import com.example.ingestion.constants.ServiceInfo;
import com.example.ingestion.dto.ServiceInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

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
            ApiPaths.NOTIFICATIONS,
            ApiPaths.SWAGGER_UI
        );
    }
}
