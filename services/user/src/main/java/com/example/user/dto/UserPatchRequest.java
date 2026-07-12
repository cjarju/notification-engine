package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.user.constants.RegExpStr;

public record UserPatchRequest(
    @Size(min = 3, max = 50) String username,
    @Email String email,
    @Pattern(regexp = RegExpStr.PHONE_NUM_E164) String phoneNumber,
    Boolean active
) {}
