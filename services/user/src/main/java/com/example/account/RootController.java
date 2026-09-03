package com.example.account;

import com.example.account.common.constants.ApiPaths;
import com.example.account.common.constants.ServiceInfo;
import com.example.account.common.dto.ServiceInfoResponse;

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
            ApiPaths.USERS,
            ApiPaths.SWAGGER_UI
        );
    }
}
