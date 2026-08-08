package com.example.account.preference.exception;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;

public class UserPreferenceAlreadyExistsException extends RuntimeException {

    public UserPreferenceAlreadyExistsException(
            AlertCategory category,
            DeliveryChannel channel) {
        this(category, channel, null);
    }

    public UserPreferenceAlreadyExistsException(
            AlertCategory category,
            DeliveryChannel channel,
            Throwable cause) {
        super(
            "A user preference for category '%s' and channel '%s' already exists."
                .formatted(category, channel),
            cause
        );
    }
}
