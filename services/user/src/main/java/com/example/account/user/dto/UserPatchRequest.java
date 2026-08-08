package com.example.account.user.dto;

import com.example.account.common.constants.RegExpStr;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchRequest(
    @Size(min = 3, max = 50, message = "{user.username.size}")
    String username,

    @Email(message = "{user.email.invalid}")
    String email,

    @Pattern(regexp = RegExpStr.PHONE_NUM_E164, message = "{user.phone.invalid}")
    String phoneNumber,

    Boolean active
) {}
