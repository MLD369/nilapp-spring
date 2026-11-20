package com.mldtech.nilapp.api.auth.dto;

public record DevResetPasswordResponse(String username, String temporaryPassword, String message) {}
