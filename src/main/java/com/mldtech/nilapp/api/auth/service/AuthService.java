package com.mldtech.nilapp.api.auth.service;

import com.mldtech.nilapp.api.auth.dto.AuthResponse;
import com.mldtech.nilapp.api.auth.dto.LoginRequest;
import com.mldtech.nilapp.api.auth.dto.RegisterRequest;
import com.mldtech.nilapp.api.auth.model.RefreshToken;
import com.mldtech.nilapp.api.auth.repository.RefreshTokenRepository;
import com.mldtech.nilapp.api.users.model.User;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final long REFRESH_TOKEN_DAYS = 30;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return issueTokensForDevice(user, request.deviceId());
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        String incomingHash = jwtService.hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(incomingHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new BadCredentialsException("Refresh token expired");
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Rotate refresh token on the same device row
        return issueTokensForDevice(user, storedToken.getDeviceId());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) return;
        String hash = jwtService.hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResponse issueTokensForDevice(User user, String deviceId) {
        String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getUsername());
        String rawRefreshToken = UUID.randomUUID() + "." + UUID.randomUUID();
        String hashedRefresh = jwtService.hashToken(rawRefreshToken);

        RefreshToken tokenEntity = refreshTokenRepository
                .findByUserIdAndDeviceId(user.getUserId(), deviceId)
                .orElse(new RefreshToken());

        tokenEntity.setUserId(user.getUserId());
        tokenEntity.setDeviceId(deviceId);
        tokenEntity.setTokenHash(hashedRefresh);
        tokenEntity.setExpiresAt(Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS));

        refreshTokenRepository.save(tokenEntity);

        return new AuthResponse(accessToken, rawRefreshToken, user.getUserId());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredSince(Instant.now());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());


        // Hash the password using BCrypt before saving to Postgres
        user.setPassword(passwordEncoder.encode(request.password()));

        // Set default values for your non-null columns if not handled by @PrePersist / DB defaults
        user.setTotalLifetimeCoins(0L);
        user.setTotalLifetimeSteps(0L);
        user.setTotalLifetimeClaimedSteps(0L);
        user.setForcePasswordChange(false);

        User savedUser = userRepository.save(user);

        // Immediately issue tokens so the user goes straight to the "Create PIN" screen
        return issueTokensForDevice(savedUser, request.deviceId());
    }

}

