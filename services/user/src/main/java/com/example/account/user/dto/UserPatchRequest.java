package com.example.account.user.dto;

import com.example.account.common.constants.RegExpStr;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchRequest(
    @Size(min = 3, max = 50) String username,
    @Email String email,
    @Pattern(regexp = RegExpStr.PHONE_NUM_E164) String phoneNumber,
    Boolean active
) {}
