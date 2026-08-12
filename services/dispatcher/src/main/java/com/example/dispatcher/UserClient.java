package com.example.dispatcher;

import com.example.dispatcher.config.UserServiceProperties;
import com.example.dispatcher.constants.ApiPaths;
import com.example.dispatcher.dto.UserPreference;
import com.example.dispatcher.exception.UserCommunicationException;
import com.example.dispatcher.exception.UserNotFoundException;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UserClient {

    private final RestClient userRestClient;

    public UserClient(
            RestClient.Builder restClientBuilder,
            UserServiceProperties properties) {

        this.userRestClient = restClientBuilder
                .baseUrl(properties.url())
                .build();
    }

    public List<UserPreference> getUserPreferences(Long userId) {
        try {
            return userRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ApiPaths.USER_PREFERENCES)
                            .queryParam("projection", "SUMMARY")
                            .build(userId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, resp) -> {
                        throw new UserNotFoundException(userId);
                    })
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        throw new UserCommunicationException(
                            "User Service returned status: " + resp.getStatusCode()
                        );
                    })
                    .body(new ParameterizedTypeReference<List<UserPreference>>() {});

        } catch (RestClientException ex) {
            throw new UserCommunicationException(
                "Failed to communicate with User Service",
                ex
            );
        }
    }
}
