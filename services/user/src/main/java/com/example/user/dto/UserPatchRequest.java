package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchRequest(
    @Size(min = 3, max = 50) String username,
    @Email String email,
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$") String phoneNumber,
    Boolean active
) {}