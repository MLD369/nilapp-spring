package com.mldtech.nilapp.api.UserStreak.dto;

import lombok.Builder;

@Builder
public record StreakRescueStatusDTO(
        boolean rescueNeeded,
        int brokenStreakCount,
        int availableShields,
        boolean canUseShield,
        boolean saverOnCooldown,
        long saverCooldownDaysRemaining,
        boolean isSaverInstantUnlocked,   // True if >= 10 claims in 7 days & not on cooldown
        boolean canStartRedemption,       // True if < 10 claims & not on cooldown
        int recentClaimsCount,
        int requiredClaimsForSaver,
        boolean isInRedemptionMode,
        int redemptionDaysCompleted,
        int frozenStreakToRestore,
        String message
) {}

