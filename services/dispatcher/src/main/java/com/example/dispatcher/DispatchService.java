package com.example.dispatcher;

import com.example.dispatcher.dto.DispatchRequest;
import com.example.dispatcher.dto.DispatchResponse;
import com.example.dispatcher.dto.UserPreference;
import com.example.dispatcher.enums.DispatchStatus;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DispatchService {

    private final UserClient userClient;

    public DispatchService(UserClient userClient) {
        this.userClient = userClient;
    }

    public DispatchResponse processDispatch(DispatchRequest request) {

        List<UserPreference> preferences =
                userClient.getUserPreferences(request.userId());

        boolean enabled = preferences.stream()
                .filter(preference ->
                        preference.category() == request.category()
                        && preference.channel() == request.channel())
                .anyMatch(UserPreference::enabled);

        if (!enabled) {
                return rejected(
                        request,
                        "Notification is disabled for category "
                                + request.category()
                                + " and channel "
                                + request.channel()
                );
        }

        return delivered(request);
    }

    private DispatchResponse delivered(DispatchRequest request) {
        return new DispatchResponse(
                request.trackingId(),
                request.userId(),
                DispatchStatus.DELIVERED,
                "Notification dispatched successfully via "
                        + request.channel(),
                OffsetDateTime.now()
        );
    }

    private DispatchResponse rejected(
        DispatchRequest request,
        String reason) {

        return new DispatchResponse(
                request.trackingId(),
                request.userId(),
                DispatchStatus.REJECTED,
                reason,
                OffsetDateTime.now()
        );
    }
}
