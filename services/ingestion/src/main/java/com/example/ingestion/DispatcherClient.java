package com.example.ingestion;

import com.example.ingestion.config.DispatcherProperties;
import com.example.ingestion.constants.ApiPaths;
import com.example.ingestion.dto.DispatchRequest;
import com.example.ingestion.dto.DispatchResponse;
import com.example.ingestion.exception.DispatcherCommunicationException;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DispatcherClient {

    private final RestClient dispatcherRestClient;

    public DispatcherClient(
            RestClient.Builder restClientBuilder,
            DispatcherProperties properties) {

        this.dispatcherRestClient = restClientBuilder
                .baseUrl(properties.url())
                .build();
    }

    public DispatchResponse dispatchNotification(DispatchRequest request) {
        return dispatcherRestClient.post()
                .uri(ApiPaths.DISPATCH)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new DispatcherCommunicationException(
                            "Dispatcher service returned status: " + resp.getStatusCode()
                    );
                })
                .body(DispatchResponse.class);
    }
}
