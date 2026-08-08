package com.example.account.user.dto;

import com.example.account.common.constants.RegExpStr;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "{user.username.required}")
    @Size(min = 3, max = 50, message = "{user.username.size}")
    String username,

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    String email,

    @Pattern(regexp = RegExpStr.PHONE_NUM_E164, message = "{user.phone.invalid}")
    String phoneNumber,

    @NotNull(message = "{user.active.required}")
    Boolean active
) {}
