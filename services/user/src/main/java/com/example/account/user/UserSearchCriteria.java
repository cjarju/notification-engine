package com.example.account.user;

public record UserSearchCriteria(
        String username,
        String email,
        boolean active
) {}
