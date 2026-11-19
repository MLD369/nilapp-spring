package com.mldtech.nilapp.api.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, Long userId) {}
