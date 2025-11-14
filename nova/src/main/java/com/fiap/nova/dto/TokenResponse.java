package com.fiap.nova.dto;

public record TokenResponse(
    String token,
    String username,
    String role
) {}
