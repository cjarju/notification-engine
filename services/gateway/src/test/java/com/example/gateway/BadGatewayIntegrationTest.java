package com.example.gateway;

import com.example.gateway.constants.HeaderConstants;
import com.example.gateway.constants.ApiPaths;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class BadGatewayIntegrationTest {

    private static final WireMockServer wireMockServer =
            new WireMockServer(wireMockConfig().dynamicPort());

    static {
        wireMockServer.start();
    }

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
        "INGESTION_SERVICE_URL",
            () -> wireMockServer.baseUrl()
        );

        registry.add(
        "USER_SERVICE_URL",
            () -> wireMockServer.baseUrl()
        );
    }

    @Test
    void request_whenUpstreamConnectionFails_returns502BadGateway() {

        String correlationId = "abc123";

        /*
         * Stop the upstream server so that the Gateway gets a
         * connection failure when it attempts the request.
         */
        wireMockServer.stop();

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
                .header(HeaderConstants.CORRELATION_ID, correlationId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "trackingId": "track-123",
                          "userId": 1001,
                          "category": "ACCOUNT",
                          "channel": "EMAIL",
                          "content": "Test content"
                        }
                        """
                )
                .exchange()
                .expectStatus()
                .isEqualTo(502)
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectHeader()
                .valueEquals(HeaderConstants.CORRELATION_ID, correlationId)
                .expectBody()
                .jsonPath("$.type")
                .isEqualTo("errors/bad-gateway")
                .jsonPath("$.title")
                .isEqualTo("Bad Gateway")
                .jsonPath("$.status")
                .isEqualTo(502)
                .jsonPath("$.detail")
                .isEqualTo("The gateway could not communicate with the upstream service.")
                .jsonPath("$.timestamp")
                .exists()
                .jsonPath("$.instance")
                .isEqualTo(ApiPaths.NOTIFICATIONS)
                .jsonPath("$.correlationId")
                .isEqualTo(correlationId);
    }
}
