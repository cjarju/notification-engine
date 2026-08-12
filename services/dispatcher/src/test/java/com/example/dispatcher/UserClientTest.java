package com.example.dispatcher;

import com.example.dispatcher.config.UserServiceProperties;
import com.example.dispatcher.constants.ApiPaths;
import com.example.dispatcher.dto.UserPreference;
import com.example.dispatcher.enums.AlertCategory;
import com.example.dispatcher.enums.DeliveryChannel;
import com.example.dispatcher.exception.UserCommunicationException;
import com.example.dispatcher.exception.UserNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
@EnableConfigurationProperties(UserServiceProperties.class)
class UserClientTest {

    @Value("${services.user.url}")
    private String userServiceBaseUrl;

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void getUserPreferences_whenUserExists_returnsUserPreferences() {
        server.expect(requestTo(userPreferencesUrl(1001L)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    [
                      {
                        "id": 1,
                        "username": "john.doe",
                        "category": "ACCOUNT",
                        "channel": "EMAIL",
                        "enabled": true
                      },
                      {
                        "id": 4,
                        "username": "john.doe",
                        "category": "MARKETING",
                        "channel": "EMAIL",
                        "enabled": false
                      },
                      {
                        "id": 2,
                        "username": "john.doe",
                        "category": "SECURITY",
                        "channel": "EMAIL",
                        "enabled": true
                      },
                      {
                        "id": 3,
                        "username": "john.doe",
                        "category": "SECURITY",
                        "channel": "SMS",
                        "enabled": true
                      }
                    ]
                    """, MediaType.APPLICATION_JSON));

        List<UserPreference> preferences = userClient.getUserPreferences(1001L);

        assertThat(preferences).hasSize(4);
        assertThat(preferences.get(0).id()).isEqualTo(1L);
        assertThat(preferences.get(0).username()).isEqualTo("john.doe");
        assertThat(preferences.get(0).category()).isEqualTo(AlertCategory.ACCOUNT);
        assertThat(preferences.get(0).channel()).isEqualTo(DeliveryChannel.EMAIL);
        assertThat(preferences.get(0).enabled()).isTrue();

        assertThat(preferences.get(1).category()).isEqualTo(AlertCategory.MARKETING);
        assertThat(preferences.get(1).enabled()).isFalse();
    }

    @Test
    void getUserPreferences_whenUserNotFound_throwsUserNotFoundException() {
        server.expect(requestTo(userPreferencesUrl(999L)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> userClient.getUserPreferences(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserPreferences_whenUserServiceFails_throwsUserCommunicationException() {
        server.expect(requestTo(userPreferencesUrl(1001L)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> userClient.getUserPreferences(1001L))
                .isInstanceOf(UserCommunicationException.class);
    }

    private String userPreferencesUrl(Long userId) {
        return userServiceBaseUrl
                + ApiPaths.USER_PREFERENCES.replace("{userId}", userId.toString())
                + "?projection=SUMMARY";
    }
}
