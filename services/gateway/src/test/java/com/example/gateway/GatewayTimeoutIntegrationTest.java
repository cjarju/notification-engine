package com.example.gateway;

import com.example.gateway.constants.HeaderConstants;
import com.example.gateway.constants.ApiPaths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class GatewayTimeoutIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock =
            WireMockExtension.newInstance()
                    .options(wireMockConfig().dynamicPort())
                    .build();

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("INGESTION_SERVICE_URL", wireMock::baseUrl);
        registry.add( "USER_SERVICE_URL", wireMock::baseUrl);
        registry.add(
        "spring.cloud.gateway.server.webflux.httpclient.response-timeout",
            () -> "500ms"
        );
    }

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @Test
    void request_whenUpstreamTimesOut_returns504GatewayTimeout() {

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(202)
                        .withFixedDelay(1500)
                )
        );

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
                .header(HeaderConstants.CORRELATION_ID, "abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "trackingId": "track-123",
                          "userId": 1001,
                          "category": "ACCOUNT",
                          "channel": "EMAIL",
                          "content": "Test content"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectHeader()
                .valueEquals(HeaderConstants.CORRELATION_ID, "abc123")
                .expectBody()
                .jsonPath("$.type")
                .isEqualTo("errors/gateway-timeout")
                .jsonPath("$.title")
                .isEqualTo("Gateway Timeout")
                .jsonPath("$.status")
                .isEqualTo(504)
                .jsonPath("$.detail")
                .isEqualTo("The upstream service did not respond within the allowed time.")
                .jsonPath("$.timestamp")
                .exists()
                .jsonPath("$.instance")
                .isEqualTo(ApiPaths.NOTIFICATIONS)
                .jsonPath("$.correlationId")
                .isEqualTo("abc123");
    }
}
