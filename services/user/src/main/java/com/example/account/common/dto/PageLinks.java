package com.example.account.common.dto;

public record PageLinks(
    String self,
    String first,
    String prev,
    String next,
    String last
) {}
