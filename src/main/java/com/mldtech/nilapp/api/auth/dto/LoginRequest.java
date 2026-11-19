package com.mldtech.nilapp.api.auth.dto;


public record LoginRequest(String username, String password, String deviceId) {}
