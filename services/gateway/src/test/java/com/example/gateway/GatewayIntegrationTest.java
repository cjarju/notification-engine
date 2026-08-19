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
class GatewayIntegrationTest {

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
        registry.add("USER_SERVICE_URL", wireMock::baseUrl);
    }

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @Test
    void notification_whenIngestionServiceSucceeds_returns202Accepted() throws Exception {

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.ACCEPTED.value())
                )
        );

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
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
                .isAccepted();

        wireMock.verify(
            postRequestedFor(
                urlEqualTo(ApiPaths.NOTIFICATIONS)
            )
        );
    }

    @Test
    void request_whenCorrelationIdIsAbsent_generatesAndPropagatesCorrelationId() {

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(202)
                )
        );

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
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
                .isAccepted()
                .expectHeader()
                .exists(HeaderConstants.CORRELATION_ID);

        wireMock.verify(
            postRequestedFor(
                urlEqualTo(ApiPaths.NOTIFICATIONS)
            )
            .withHeader(
                HeaderConstants.CORRELATION_ID,
                matching(".+")
            )
        );
    }

    @Test
    void request_whenCorrelationIdExists_preservesAndPropagatesCorrelationId() {

        String correlationId = "abc123";

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(202)
                )
        );

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
                        """)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectHeader()
                .valueEquals(HeaderConstants.CORRELATION_ID, correlationId);

        wireMock.verify(
            postRequestedFor(
                urlEqualTo(ApiPaths.NOTIFICATIONS)
            )
            .withHeader(
                HeaderConstants.CORRELATION_ID,
                equalTo(correlationId)
            )
        );
    }

    @Test
    void request_whenDownstreamReturns400_proxiesStatusAndBody() {

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "title": "Validation failed",
                                  "detail": "Invalid notification"
                                }
                                """)
                )
        );

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "trackingId": "track-123"
                        }
                        """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.title")
                .isEqualTo("Validation failed")
                .jsonPath("$.detail")
                .isEqualTo("Invalid notification");
    }

    @Test
    void request_whenDownstreamReturns500_proxiesStatus() {

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                )
        );

        webTestClient.post()
                .uri(ApiPaths.NOTIFICATIONS)
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
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void user_whenUserServiceSucceeds_proxiesResponse() {

        wireMock.stubFor(
            get(urlEqualTo(ApiPaths.USERS + "/1001"))
                .willReturn(
                    okJson("""
                        {
                          "id": 1001,
                          "email": "user@example.com"
                        }
                        """)
                )
        );

        webTestClient.get()
                .uri(ApiPaths.USERS + "/1001")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(1001)
                .jsonPath("$.email")
                .isEqualTo("user@example.com");

        wireMock.verify(
            getRequestedFor(
                urlEqualTo(ApiPaths.USERS + "/1001")
            )
        );
    }

    @Test
    void notification_whenIngestionReturns504_proxiesStatusAndBody() {
        String responseBody = """
                {
                  "type": "errors/gateway-timeout",
                  "title": "Service Unavailable",
                  "status": 504,
                  "detail": "Ingestion service failed"
                }
                """;

        wireMock.stubFor(
            post(urlEqualTo(ApiPaths.NOTIFICATIONS))
                .willReturn(
                    aResponse()
                        .withStatus(504)
                        .withHeader("Content-Type","application/problem+json")
                        .withBody(responseBody)
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
                .isEqualTo("Service Unavailable")
                .jsonPath("$.status")
                .isEqualTo(504)
                .jsonPath("$.detail")
                .isEqualTo("Ingestion service failed");
    }
}
