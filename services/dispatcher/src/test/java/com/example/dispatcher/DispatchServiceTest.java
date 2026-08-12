package com.example.dispatcher;

import com.example.dispatcher.dto.DispatchRequest;
import com.example.dispatcher.dto.DispatchResponse;
import com.example.dispatcher.dto.UserPreference;
import com.example.dispatcher.enums.AlertCategory;
import com.example.dispatcher.enums.DeliveryChannel;
import com.example.dispatcher.enums.DispatchStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private DispatchService dispatchService;

    private DispatchRequest defaultRequest;

    @BeforeEach
    void setUp() {
        defaultRequest = new DispatchRequest(
                "track-123",
                1001L,
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                "Your password was changed",
                null
        );
    }

    @Test
    void processDispatch_whenMatchingPreferenceIsEnabled_returnsDeliveredStatus() {
        UserPreference preference = new UserPreference(
                1L,
                "john.doe",
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                true
        );

        when(userClient.getUserPreferences(1001L))
                .thenReturn(List.of(preference));

        DispatchResponse response = dispatchService.processDispatch(defaultRequest);

        assertThat(response.status()).isEqualTo(DispatchStatus.DELIVERED);
        assertThat(response.trackingId()).isEqualTo("track-123");
        assertThat(response.userId()).isEqualTo(1001L);
        assertThat(response.message()).contains("dispatched successfully");

        verify(userClient).getUserPreferences(1001L);
    }

    @Test
    void processDispatch_whenMatchingPreferenceIsDisabled_returnsRejectedStatus() {
        UserPreference preference = new UserPreference(
                1L,
                "john.doe",
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                false
        );

        when(userClient.getUserPreferences(1001L))
                .thenReturn(List.of(preference));

        DispatchResponse response = dispatchService.processDispatch(defaultRequest);

        assertThat(response.status()).isEqualTo(DispatchStatus.REJECTED);
        assertThat(response.trackingId()).isEqualTo("track-123");
        assertThat(response.userId()).isEqualTo(1001L);
        assertThat(response.message()).isEqualTo(
                "Notification is disabled for category ACCOUNT and channel EMAIL"
        );

        verify(userClient).getUserPreferences(1001L);
    }

    @Test
    void processDispatch_whenNoMatchingPreferenceExists_returnsRejectedStatus() {
        UserPreference preference = new UserPreference(
                2L,
                "john.doe",
                AlertCategory.MARKETING,
                DeliveryChannel.EMAIL,
                true
        );

        when(userClient.getUserPreferences(1001L))
                .thenReturn(List.of(preference));

        DispatchResponse response = dispatchService.processDispatch(defaultRequest);

        assertThat(response.status()).isEqualTo(DispatchStatus.REJECTED);
        assertThat(response.trackingId()).isEqualTo("track-123");
        assertThat(response.userId()).isEqualTo(1001L);
        assertThat(response.message()).isEqualTo(
                "Notification is disabled for category ACCOUNT and channel EMAIL"
        );

        verify(userClient).getUserPreferences(1001L);
    }

    @Test
    void processDispatch_whenUserHasMultiplePreferences_usesMatchingPreference() {
        UserPreference marketingPreference = new UserPreference(
                4L,
                "john.doe",
                AlertCategory.MARKETING,
                DeliveryChannel.EMAIL,
                false
        );

        UserPreference accountPreference = new UserPreference(
                1L,
                "john.doe",
                AlertCategory.ACCOUNT,
                DeliveryChannel.EMAIL,
                true
        );

        UserPreference securityPreference = new UserPreference(
                2L,
                "john.doe",
                AlertCategory.SECURITY,
                DeliveryChannel.EMAIL,
                true
        );

        when(userClient.getUserPreferences(1001L))
                .thenReturn(List.of(
                        marketingPreference,
                        accountPreference,
                        securityPreference
                ));

        DispatchResponse response = dispatchService.processDispatch(defaultRequest);

        assertThat(response.status()).isEqualTo(DispatchStatus.DELIVERED);
        assertThat(response.trackingId()).isEqualTo("track-123");
        assertThat(response.userId()).isEqualTo(1001L);

        verify(userClient).getUserPreferences(1001L);
    }
}
