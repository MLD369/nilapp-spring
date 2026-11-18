package com.mldtech.nilapp.api.UserStreak.service;

import com.mldtech.nilapp.api.UserStreak.model.UserStreak;
import com.mldtech.nilapp.api.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.users.dto.ShieldEligibilityDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static com.mldtech.nilapp.helper.AppConstants.*;

@Service
@RequiredArgsConstructor
public class UserStreakService {
    private final UserStreakRepository userStreakRepository;
    private static final Logger log = LoggerFactory.getLogger(UserStreakService.class);

    /**
     * FRONTEND CHECK API: Returns whether a user is eligible to earn a shield (under the 3-shield cap)
     * and how many consecutive clean days remain until their next shield milestone.
     */
    /**
     * 1. GET CHECK API: Tells the UI whether to show the "Claim Shield" button or milestone progress.
     */
    public ShieldEligibilityDTO checkShieldEligibility(Long userId) {
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        int currentShields = streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0;
        int cleanDays = streakRecord.getCleanStreakDays() != null ? streakRecord.getCleanStreakDays() : 0;
        boolean unlocked = Boolean.TRUE.equals(streakRecord.getShieldClaimable());

        if (unlocked && currentShields < MAX_SHIELDS) {
            return ShieldEligibilityDTO.builder()
                    .canClaimShieldNow(true)
                    .shieldUnlocked(true)
                    .currentShields(currentShields)
                    .maxShields(MAX_SHIELDS)
                    .cleanStreakDays(cleanDays)
                    .nextMilestoneDays(cleanDays)
                    .daysRemainingToShield(0)
                    .message("You unlocked a Streak Shield! Tap to claim it now.")
                    .build();
        }

        if (unlocked && currentShields >= MAX_SHIELDS) {
            return ShieldEligibilityDTO.builder()
                    .canClaimShieldNow(false)
                    .shieldUnlocked(true)
                    .currentShields(currentShields)
                    .maxShields(MAX_SHIELDS)
                    .cleanStreakDays(cleanDays)
                    .nextMilestoneDays(0)
                    .daysRemainingToShield(0)
                    .message("You earned a shield, but your inventory is full (3/3). Use one to claim this reward!")
                    .build();
        }

        int nextMilestone = ORDERED_MILESTONES.stream()
                .filter(m -> m > cleanDays)
                .findFirst()
                .orElse(((cleanDays / 100) + 1) * 100);

        int daysRemaining = nextMilestone - cleanDays;

        return ShieldEligibilityDTO.builder()
                .canClaimShieldNow(false)
                .shieldUnlocked(false)
                .currentShields(currentShields)
                .maxShields(MAX_SHIELDS)
                .cleanStreakDays(cleanDays)
                .nextMilestoneDays(nextMilestone)
                .daysRemainingToShield(daysRemaining)
                .message(String.format("Complete %d more consecutive day(s) without a saver to unlock a shield.", daysRemaining))
                .build();
    }

    /**
     * 2. POST CLAIM API: Called when the user clicks "Claim Shield" on the UI.
     * Freezes the row (FOR UPDATE) to prevent double-claiming and enforces the 3-shield cap.
     */
    @Transactional
    public ShieldEligibilityDTO claimUnlockedShield(Long userId) {
        UserStreak streakRecord = userStreakRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        if (!Boolean.TRUE.equals(streakRecord.getShieldClaimable())) {
            throw new IllegalStateException("No unlocked Streak Shield is currently available to claim.");
        }

        int currentShields = streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0;
        if (currentShields >= MAX_SHIELDS) {
            throw new IllegalStateException("Shield inventory is already full (3/3). Use a shield before claiming another.");
        }

        streakRecord.setStreakShields(currentShields + 1);
        streakRecord.setTotalShieldsEarned(
                (streakRecord.getTotalShieldsEarned() != null ? streakRecord.getTotalShieldsEarned() : 0) + 1
        );
        streakRecord.setShieldClaimable(false);
        streakRecord.setUpdatedAt(LocalDateTime.now());

        userStreakRepository.save(streakRecord);

        return checkShieldEligibility(userId);
    }



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
                    "cleanStreakDays", streakRecord.getCleanStreakDays(),
                    "previousStreak", 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", streakRecord.getLastCompletedDate(),
                    "longestStreak", streakRecord.getLongestStreak(),
                    "message", "Your streak is secure."
            );
        }

        // Condition B: Check if they successfully hit their metric totals yesterday
        long totalAttempts = userStreakRepository.countYesterdayTotalClaimsAttempts(userId);
        if (totalAttempts >= AMOUNT_FOR_STREAK) {
            boolean shieldAwardedToday = false;

            // IDEMPOTENCY GUARD: Only increment and save if they haven't already processed today's milestone check
            if (lastCompleted == null || !lastCompleted.equals(today)) {
                int newStreak = streakRecord.getCurrentStreak() + 1;
                int newCleanStreak = (streakRecord.getCleanStreakDays() != null ? streakRecord.getCleanStreakDays() : 0) + 1;

                streakRecord.setCurrentStreak(newStreak);
                streakRecord.setCleanStreakDays(newCleanStreak);
                streakRecord.setLastCompletedDate(today);
                streakRecord.setSoftResetAt(null);

                // Award shield on exact clean milestones (3, 7, 14, 30, 60, 100, etc.) if under the 3-shield cap
                int currentShields = streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0;
                if (isShieldMilestone(newCleanStreak) && currentShields < MAX_SHIELDS) {
                    streakRecord.setStreakShields(currentShields + 1);
                    streakRecord.setTotalShieldsEarned(streakRecord.getTotalShieldsEarned() + 1);
                    shieldAwardedToday = true;
                    log.info("User ID {} earned a Streak Shield at {} clean days! Inventory: {}/{}",
                            userId, newCleanStreak, streakRecord.getStreakShields(), MAX_SHIELDS);
                }

                // 📈 LONGEST STREAK RECORD-BREAKER ENGINE
                if (newStreak > streakRecord.getLongestStreak()) {
                    streakRecord.setLongestStreak(newStreak);
                    log.info("User ID {} just broke their personal record! New Longest Streak: {}", userId, newStreak);
                }

                userStreakRepository.save(streakRecord);
            }

            return Map.ofEntries(
                    Map.entry("status", "SAFE"),
                    Map.entry("actionRequired", false),
                    Map.entry("currentStreak", streakRecord.getCurrentStreak()),
                    Map.entry("cleanStreakDays", streakRecord.getCleanStreakDays()),
                    Map.entry("previousStreak", 0),
                    Map.entry("availableShields", streakRecord.getStreakShields()),
                    Map.entry("shieldAwardedToday", shieldAwardedToday),
                    Map.entry("lastCompletedDate", today),
                    Map.entry("totalAttempts", totalAttempts),
                    Map.entry("longestStreak", streakRecord.getLongestStreak()),
                    Map.entry("message", shieldAwardedToday
                            ? "Daily goal achieved! Streak increased and you earned a new Streak Shield!"
                            : "Daily goal achieved! Streak increased.")
            );

        }


        // Condition C: Retroactive Recovery Window Check (User accepted reset TODAY)
        if (softResetAt != null && softResetAt.equals(today)) {
            return Map.of(
                    "status", "STREAK_SOFT_RESET",
                    "actionRequired", false,
                    "currentStreak", 0,
                    "cleanStreakDays", 0,
                    "previousStreak", streakRecord.getPreviousStreak(),
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", today,
                    "totalAttempts", totalAttempts,
                    "longestStreak", streakRecord.getLongestStreak(),
                    "message", "You accepted a reset today, but you can still recover your streak before midnight!"
            );
        }

        // Hard Reset (User missed 2 or more days)
        LocalDate twoDaysAgo = today.minusDays(2);
        if (lastCompleted == null || lastCompleted.isBefore(twoDaysAgo)) {
            streakRecord.setCurrentStreak(0);
            streakRecord.setCleanStreakDays(0);
            streakRecord.setSoftResetAt(null);
            userStreakRepository.save(streakRecord);

            return Map.of(
                    "status", "STREAK_BROKEN",
                    "actionRequired", false,
                    "currentStreak", 0,
                    "cleanStreakDays", 0,
                    "previousStreak", 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", lastCompleted != null ? lastCompleted : twoDaysAgo,
                    "totalAttempts", totalAttempts,
                    "longestStreak", streakRecord.getLongestStreak(),
                    "message", "Your streak has reset because you missed multiple days."
            );
        }

        // Condition D: Action Required (First-time break notification)
        return Map.of(
                "status", "STREAK_IN_DANGER",
                "actionRequired", true,
                "currentStreak", streakRecord.getCurrentStreak(),
                "cleanStreakDays", streakRecord.getCleanStreakDays(),
                "previousStreak", 0,
                "availableShields", streakRecord.getStreakShields(),
                "lastCompletedDate", lastCompleted != null ? lastCompleted : yesterday.minusDays(1),
                "totalAttempts", totalAttempts,
                "longestStreak", streakRecord.getLongestStreak(),
                "message", "You did not complete your 10 ad claims yesterday."
        );
    }

    /**
     * Executes the user's manual option choice from the app pop-up interface.
     * Strictly hardened against repeat-execution exploits on the same calendar day.
     */
    @Transactional
    public void executeStreakRescueSelection(Long userId, String selectedOption) {
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate lastCompleted = streakRecord.getLastCompletedDate();

        // 🔒 THE API EXPLOIT SECURITY GATE
        if (lastCompleted != null && lastCompleted.equals(today)) {
            throw new IllegalStateException(
                    "Security Block: You have already secured or rescued your daily streak today. " +
                            "Action option '" + selectedOption + "' has been rejected."
            );
        }

        // 2. Determine if we are recovering from an unfinalized soft reset written earlier today
        boolean isRecovering = streakRecord.getSoftResetAt() != null && streakRecord.getSoftResetAt().equals(today);
        int targetStreakValue = isRecovering ? streakRecord.getPreviousStreak() : streakRecord.getCurrentStreak();
        switch (selectedOption) {
            case "USE_SHIELD_SHORT_AD":
                int rowsUpdated = userStreakRepository.consumeShieldWithShortAd(userId);
                if (rowsUpdated == 0) {
                    throw new RuntimeException("Action failed. No available shields in inventory.");
                }
                // Restores currentStreak, clears soft_reset_at, locks today, and resets clean_streak_days to 0
                userStreakRepository.restoreStreakState(userId, targetStreakValue, today);
                break;
            case "WATCH_PREMIUM_LONG_AD":
                userStreakRepository.consumePremiumVideoSaver(userId, targetStreakValue);
                // Restores currentStreak, clears soft_reset_at, locks today, and resets clean_streak_days to 0
                userStreakRepository.restoreStreakState(userId, targetStreakValue, today);
                break;
            case "ACCEPT_RESET":
                if (!isRecovering) {
                // Soft resets streak and resets clean_streak_days to 0
                    userStreakRepository.softResetActiveStreak(userId, streakRecord.getCurrentStreak(), today);
                } else {
                    throw new IllegalStateException("Your streak has already been soft-reset today. Awaiting rescue options.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown rescue selection option pattern: " + selectedOption);
        }
    }
    private boolean isShieldMilestone(int cleanDays) {
        return cleanDays > 0 && (MILESTONE_SET.contains(cleanDays) || (cleanDays > 100 && cleanDays % 100 == 0));
    }
}