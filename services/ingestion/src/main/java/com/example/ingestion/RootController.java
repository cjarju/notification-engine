package com.example.ingestion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ingestion.constants.ApiPaths;
import com.example.ingestion.constants.ServiceInfo;
import com.example.ingestion.dto.ServiceInfoResponse;

@RestController
public class RootController {

    @GetMapping("/")
    public ServiceInfoResponse root() {
        return new ServiceInfoResponse(
            ServiceInfo.NAME,
            ServiceInfo.DESCRIPTION,
            ServiceInfo.VERSION,
            ApiPaths.NOTIFICATIONS,
            ApiPaths.SWAGGER_UI
        );
    }
}
