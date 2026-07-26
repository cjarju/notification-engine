package com.example.account;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.common.constants.ApiPaths;
import com.example.account.common.constants.ServiceInfo;
import com.example.account.common.dto.ServiceInfoResponse;

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
