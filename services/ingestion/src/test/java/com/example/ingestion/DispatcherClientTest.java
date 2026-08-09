package com.example.ingestion;

import com.example.ingestion.dto.DispatchRequest;
import com.example.ingestion.dto.DispatchResponse;
import com.example.ingestion.exception.DispatcherCommunicationException;
import com.example.ingestion.enums.AlertCategory;
import com.example.ingestion.enums.DeliveryChannel;
import com.example.ingestion.config.DispatcherProperties;
import com.example.ingestion.constants.ApiPaths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(DispatcherClient.class)
@EnableConfigurationProperties(DispatcherProperties.class)
class DispatcherClientTest {

    @Value("${services.dispatcher.url}")
    private String dispatcherBaseUrl;

    @Autowired
    private DispatcherClient dispatcherClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void dispatch_whenDownstreamReturns200_returnsDispatchResponse() {
        server.expect(requestTo(dispatcherBaseUrl + ApiPaths.DISPATCH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                    {"trackingId": "123", "status": "DELIVERED"}
                    """, MediaType.APPLICATION_JSON));

        DispatchRequest request = new DispatchRequest(
                "test-tracking-id",
                1001L,
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                "Hello",
                null
        );

        DispatchResponse response = dispatcherClient.dispatchNotification(request);

        assertThat(response.trackingId()).isEqualTo("123");
    }

    @Test
    void dispatch_whenDownstreamReturns500_throwsDispatcherCommunicationException() {
        server.expect(requestTo(dispatcherBaseUrl + ApiPaths.DISPATCH))
                .andRespond(withServerError());

        DispatchRequest request = new DispatchRequest(
                "test-tracking-id",
                1001L,
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                "Hello",
                null
        );

        assertThatThrownBy(() -> dispatcherClient.dispatchNotification(request))
                .isInstanceOf(DispatcherCommunicationException.class);
    }
}
