package com.mldtech.nilapp.api.users.children.UserStreak.service;

import com.mldtech.nilapp.api.users.children.UserStreak.model.UserStreak;
import com.mldtech.nilapp.api.users.children.UserStreak.repository.UserStreakRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserStreakService {
    private final UserStreakRepository userStreakRepository;

    private final int amountForStreak = 10;

    /**
     * Evaluates a user's streak status immediately when they open the app.
     * Custom-tailored to handle retroactive soft-reset recovery windows.
     */
    @Transactional
    public Map<String, Object> checkStreakSafetyOnAppOpen(Long userId) {
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastCompleted = streakRecord.getLastCompletedDate();
        LocalDate softResetAt = streakRecord.getSoftResetAt();

        // Condition A: User already secured or rescued their streak cleanly
        if (lastCompleted != null && (lastCompleted.equals(today) || lastCompleted.equals(yesterday))) {
            return Map.of(
                    "status", "SAFE",
                    "actionRequired", false,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "previousStreak", 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", streakRecord.getLastCompletedDate(),
                    "message", "Your streak is secure."
            );
        }

        // Condition B: Check if they successfully hit their metric totals yesterday
        long totalAttempts = userStreakRepository.countYesterdayTotalClaimsAttempts(userId);
        if (totalAttempts >= amountForStreak) {
            if (lastCompleted == null || !lastCompleted.equals(today)) {
                streakRecord.setCurrentStreak(streakRecord.getCurrentStreak() + 1);
                streakRecord.setLastCompletedDate(today);
                streakRecord.setSoftResetAt(null);
                streakRecord.setPreviousStreak(0);// Clear out old reset histories if any existed
                userStreakRepository.save(streakRecord);
            }
            return Map.of(
                    "status", "SAFE",
                    "actionRequired", false,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "previousStreak", 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", today,
                    "totalAttempts", totalAttempts,
                    "message", "Daily goal achieved! Streak increased."
            );
        }

        // Condition C: Retroactive Recovery Window Check (User accepted reset TODAY)
        if (softResetAt != null && softResetAt.equals(today)) {
            return Map.of(
                    "status", "STREAK_SOFT_RESET",
                    "actionRequired", false, // false so the modal doesn't force pop automatically
                    "currentStreak", 0,      // Shows 0 on dashboard
                    "previousStreak", streakRecord.getPreviousStreak(), // Pass back what they can win back!
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", today,
                    "totalAttempts", totalAttempts,
                    "message", "You accepted a reset today, but you can still recover your streak before midnight!"
            );
        }

        // Condition D: Action Required (First-time break notification)
        return Map.of(
                "status", "STREAK_IN_DANGER",
                "actionRequired", true,
                "currentStreak", streakRecord.getCurrentStreak(),
                "previousStreak", 0,
                "availableShields", streakRecord.getStreakShields(),
                "lastCompletedDate", lastCompleted != null ? lastCompleted : yesterday.minusDays(1),
                "totalAttempts", totalAttempts,
                "message", "You did not complete your 10 ad claims yesterday."
        );
    }

    /**
     * Executes the user's manual option choice from the app pop-up interface.
     * Strictly hardened against repeat-execution exploits on the same calendar day.
     */
    @Transactional
    public void executeStreakRescueSelection(Long userId, String selectedOption) {
        // 1. Fetch the user's active streak record straight from the master database
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate lastCompleted = streakRecord.getLastCompletedDate();

        // ======================================================================
        // 🔒 THE API EXPLOIT SECURITY GATE
        // ======================================================================
        // If today is already marked as completed, their daily status is locked.
        // This stops users from cycling back and forth between reset and ad-watching.
        if (lastCompleted != null && lastCompleted.equals(today)) {
            throw new IllegalStateException(
                    "Security Block: You have already secured or rescued your daily streak today. " +
                            "Action option '" + selectedOption + "' has been rejected."
            );
        }

        // 2. Determine if we are recovering from an unfinalized soft reset written earlier today
        boolean isRecovering = streakRecord.getSoftResetAt() != null && streakRecord.getSoftResetAt().equals(today);
        int targetStreakValue = isRecovering ? streakRecord.getPreviousStreak() : streakRecord.getCurrentStreak();

        // 3. Direct evaluation tree mapping straight to database operations
        switch (selectedOption) {
            case "USE_SHIELD_SHORT_AD":
                int rowsUpdated = userStreakRepository.consumeShieldWithShortAd(userId);
                if (rowsUpdated == 0) {
                    throw new RuntimeException("Action failed. No available shields in inventory.");
                }
                // Restores the previous value, clears the soft_reset_at timestamp, and locks today's window
                userStreakRepository.restoreStreakState(userId, targetStreakValue, today);
                break;

            case "WATCH_PREMIUM_LONG_AD":
                userStreakRepository.consumePremiumVideoSaver(userId, targetStreakValue);
                // Restores the previous value, clears the soft_reset_at timestamp, and locks today's window
                userStreakRepository.restoreStreakState(userId, targetStreakValue, today);
                break;

            case "ACCEPT_RESET":
                // Only allow an initial soft reset. If they have already done a soft reset today, block duplicate triggers.
                if (!isRecovering) {
                    userStreakRepository.softResetActiveStreak(userId, streakRecord.getCurrentStreak(), today);
                } else {
                    throw new IllegalStateException("Your streak has already been soft-reset today. Awaiting rescue options.");
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown rescue selection option pattern: " + selectedOption);
        }
    }

}