package com.example.account.preference.exception;

public class UserPreferenceNotFoundException extends RuntimeException {

    public UserPreferenceNotFoundException(Long preferenceId) {
        super("User preference not found for ID: " + preferenceId);
    }

}
