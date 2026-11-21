package com.mldtech.nilapp.enums;

public enum RescueChoice {
    GIVE_UP,                  // Reset streak to 0 (saves previousStreak)
    USE_SHIELD,               // Spend 1 Streak Shield (keeps streak + keeps cleanStreakDays)
    USE_SAVER_INSTANT,        // Path A: Unlocked (10 claims in 7 days) + watched ad -> instant restore
    START_SAVER_REDEMPTION    // Path B: Not unlocked -> freeze streak and start 7-day challenge
}

