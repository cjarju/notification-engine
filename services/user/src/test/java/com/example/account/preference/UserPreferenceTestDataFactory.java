package com.example.account.preference;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;
import com.example.account.user.User;

import java.util.Arrays;
import java.util.List;

public final class UserPreferenceTestDataFactory {

    private UserPreferenceTestDataFactory() {
    }

    public static UserPreference userPreference(
            User user,
            AlertCategory category,
            DeliveryChannel channel) {

        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setCategory(category);
        preference.setChannel(channel);

        return preference;
    }

    public static List<UserPreference> userPreferences(
            User user,
            AlertCategory category,
            DeliveryChannel... channels) {

        return Arrays.stream(channels)
                .map(channel -> userPreference(user, category, channel))
                .toList();
    }

}
