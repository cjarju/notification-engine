package com.example.dispatcher;

import com.example.dispatcher.constants.ApiPaths;
import com.example.dispatcher.constants.ServiceInfo;
import com.example.dispatcher.dto.ServiceInfoResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ServiceInfoResponse getInfo() {
        return new ServiceInfoResponse(
            ServiceInfo.NAME,
            ServiceInfo.DESCRIPTION,
            ServiceInfo.VERSION,
            ApiPaths.DISPATCH,
            ApiPaths.SWAGGER_UI
        );
    }
}
