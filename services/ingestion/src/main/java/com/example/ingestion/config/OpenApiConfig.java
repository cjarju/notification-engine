package com.example.ingestion.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8081")
                        .description("Direct base URL"),
                    new Server()
                        .url("http://localhost:8080")
                        .description("Gateway-facing base URL")
                ));
    }

    @Bean
    public OperationCustomizer globalResponsesCustomizer() {
        return (operation, handlerMethod) -> {

            ApiResponses responses = operation.getResponses();

            responses.addApiResponse("200",
                    new ApiResponse()
                            .description("OK"));

            responses.addApiResponse("202",
                    new ApiResponse()
                            .description("Accepted"));

            responses.addApiResponse("400",
                    new ApiResponse()
                            .description("Bad Request"));

            responses.addApiResponse("404",
                    new ApiResponse()
                            .description("Not Found"));

            responses.addApiResponse("500",
                    new ApiResponse()
                            .description("Internal Server Error"));

            responses.addApiResponse("502",
                    new ApiResponse()
                            .description("Bad Gateway"));

            return operation;
        };
    }
}
