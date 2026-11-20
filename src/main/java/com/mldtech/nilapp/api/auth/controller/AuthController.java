package com.mldtech.nilapp.api.auth.controller;

import com.mldtech.nilapp.api.auth.dto.*;
import com.mldtech.nilapp.api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

   // TODO temperory reset password will be upgraded to
   //  Now (MVP & Beta): Use Resend  beta
   //  Production AWS SES
    @PostMapping("/dev-reset-password")
    public ResponseEntity<DevResetPasswordResponse> devResetPassword(
            @RequestBody DevResetPasswordRequest request) {
        return ResponseEntity.ok(authService.devResetPassword(request.usernameOrEmail()));
    }


}
