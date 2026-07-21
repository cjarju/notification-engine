package com.example.user;

public record UserSearchCriteria(
        String username,
        String email,
        Boolean active
) {}
