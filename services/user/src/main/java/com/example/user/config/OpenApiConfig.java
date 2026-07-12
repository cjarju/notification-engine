package com.example.user.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer globalResponsesCustomizer() {
        return (operation, handlerMethod) -> {

            ApiResponses responses = operation.getResponses();

            responses.addApiResponse("200",
                    new ApiResponse()
                            .description("OK"));

            responses.addApiResponse("201",
                    new ApiResponse()
                            .description("Created"));

            responses.addApiResponse("204",
                    new ApiResponse()
                            .description("No Content"));

            responses.addApiResponse("400",
                    new ApiResponse()
                            .description("Bad Request"));

            responses.addApiResponse("404",
                    new ApiResponse()
                            .description("Not Found"));

            responses.addApiResponse("409",
                    new ApiResponse()
                            .description("Conflict"));

            responses.addApiResponse("500",
                    new ApiResponse()
                            .description("Internal Server Error"));

            return operation;
        };
    }
}
