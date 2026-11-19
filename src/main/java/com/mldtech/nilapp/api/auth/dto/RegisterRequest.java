package com.mldtech.nilapp.api.auth.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String deviceId
) {}

