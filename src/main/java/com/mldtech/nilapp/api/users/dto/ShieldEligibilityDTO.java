package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShieldEligibilityDTO {
    private boolean canClaimShieldNow;  // True when batch unlocked a shield AND currentShields < 3
    private boolean shieldUnlocked;     // True if batch unlocked a shield (even if inventory is 3/3)
    private int currentShields;         // Current inventory (0 to 3)
    private int maxShields;             // 3
    private int cleanStreakDays;        // Consecutive clean days
    private int nextMilestoneDays;      // Next milestone target (3, 7, 14, 30, 60, 100...)
    private int daysRemainingToShield;  // Days left until next milestone
    private String message;             // Friendly UI message
}


