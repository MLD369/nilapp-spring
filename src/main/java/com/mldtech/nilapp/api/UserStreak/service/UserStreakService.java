package com.mldtech.nilapp.api.UserStreak.service;

import com.mldtech.nilapp.api.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.UserAchievement.repository.UserAchievementRepository;
import com.mldtech.nilapp.api.UserStreak.model.UserStreak;
import com.mldtech.nilapp.api.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.users.dto.ShieldEligibilityDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.mldtech.nilapp.helper.AppConstants.*;

@Service
@RequiredArgsConstructor
public class UserStreakService {
    private final AchievementRepository achievementRepository;
    private final ContributionRepository contributionRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final UserStreakRepository userStreakRepository;

    private static final Logger log = LoggerFactory.getLogger(UserStreakService.class);

    // TODO move to constants
    private static final int REQUIRED_CLAIMS_FOR_SAVER = 10;
    private static final int CLAIM_WINDOW_DAYS = 7;
    private static final int REDEMPTION_TARGET_DAYS = 7;
    private static final int SAVER_COOLDOWN_DAYS = 30; // Tweak to 15 (2x/mo), 30 (1x/mo), or 90 (1x/3mo)
    private static final int MAX_SHIELDS = 3;
    private static final Long SHIELD_HOARDER_ACHIEVEMENT_ID = 1L;
    private static final int REQUIRED_CONSISTENT_DAYS_FOR_SAVER = 7;

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
                    .canClaimShieldNow(true)
                    .shieldUnlocked(true)
                    .currentShields(currentShields)
                    .maxShields(MAX_SHIELDS)
                    .cleanStreakDays(cleanDays)
                    .nextMilestoneDays(cleanDays)
                    .daysRemainingToShield(0)
                    .message("Inventory full (3/3)! Tap to claim the Shield Hoarder badge instead.")
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
        boolean awardedHoarderBadge = false;

        if (currentShields >= MAX_SHIELDS) {
            awardShieldHoarderAchievement(userId);
            awardedHoarderBadge = true;
        } else {
            streakRecord.setStreakShields(currentShields + 1);
            streakRecord.setTotalShieldsEarned(
                    (streakRecord.getTotalShieldsEarned() != null ? streakRecord.getTotalShieldsEarned() : 0) + 1
            );
        }

        streakRecord.setShieldClaimable(false);
        streakRecord.setUpdatedAt(LocalDateTime.now());
        userStreakRepository.save(streakRecord);

        ShieldEligibilityDTO response = checkShieldEligibility(userId);

        String claimMessage = awardedHoarderBadge
                ? "Shields full (3/3)! You earned the Shield Hoarder badge!"
                : "Streak Shield claimed! (" + (currentShields + 1) + "/3)";

        return ShieldEligibilityDTO.builder()
                .canClaimShieldNow(response.isCanClaimShieldNow())
                .shieldUnlocked(response.isShieldUnlocked())
                .currentShields(response.getCurrentShields())
                .maxShields(response.getMaxShields())
                .cleanStreakDays(response.getCleanStreakDays())
                .nextMilestoneDays(response.getNextMilestoneDays())
                .daysRemainingToShield(response.getDaysRemainingToShield())
                .message(claimMessage)
                .build();
    }


    private void awardShieldHoarderAchievement(Long userId) {
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(userRepository.getReferenceById(userId));
        userAchievement.setAchievement(achievementRepository.getReferenceById(SHIELD_HOARDER_ACHIEVEMENT_ID));
        userAchievementRepository.save(userAchievement);
    }




    /**
     * Evaluates a user's streak status immediately when they open the app.
     * Custom-tailored to handle retroactive soft-reset recovery windows.
     */
    @Transactional
    public Map<String, Object> checkStreakSafetyOnAppOpen(Long userId) {
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));
        String userTz = resolveUserTimezone(streakRecord);
        LocalDate today = LocalDate.now(ZoneId.of(userTz));
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastCompleted = streakRecord.getLastCompletedDate();
        LocalDate softResetAt = streakRecord.getSoftResetAt();
        boolean inRedemption = Boolean.TRUE.equals(streakRecord.getIsInRedemptionMode());

        // Condition A: User already secured or rescued their streak cleanly
        if (lastCompleted != null && (lastCompleted.equals(today) || lastCompleted.equals(yesterday))) {
            // Check if they have already reached 7/7 days in Redemption Mode and need their streak restored now
            if (inRedemption && streakRecord.getRedemptionDaysCompleted() != null
                    && streakRecord.getRedemptionDaysCompleted() >= REDEMPTION_TARGET_DAYS) {
                int restoredStreak = streakRecord.getFrozenStreakToRestore() + streakRecord.getRedemptionDaysCompleted();
                streakRecord.setCurrentStreak(restoredStreak);
                streakRecord.setLastSaverUsedAt(today);
                streakRecord.setIsInRedemptionMode(false);
                streakRecord.setRedemptionDaysCompleted(0);
                streakRecord.setFrozenStreakToRestore(0);

                if (restoredStreak > streakRecord.getLongestStreak()) {
                    streakRecord.setLongestStreak(restoredStreak);
                }
                userStreakRepository.save(streakRecord);

                return Map.ofEntries(
                        Map.entry("status", "SAFE"),
                        Map.entry("actionRequired", false),
                        Map.entry("redemptionCompleted", true),
                        Map.entry("currentStreak", streakRecord.getCurrentStreak()),
                        Map.entry("cleanStreakDays", streakRecord.getCleanStreakDays()),
                        Map.entry("previousStreak", 0),
                        Map.entry("availableShields", streakRecord.getStreakShields()),
                        Map.entry("lastCompletedDate", streakRecord.getLastCompletedDate()),
                        Map.entry("longestStreak", streakRecord.getLongestStreak()),
                        Map.entry("isInRedemptionMode", false),
                        Map.entry("redemptionDaysCompleted", 0),
                        Map.entry("frozenStreakToRestore", 0),
                        Map.entry("message", "7-Day Redemption Challenge complete! Your full " + restoredStreak + "-day streak has been restored!")
                );
            }

            return Map.ofEntries(
                    Map.entry("status", "SAFE"),
                    Map.entry("actionRequired", false),
                    Map.entry("redemptionCompleted", false),
                    Map.entry("currentStreak", streakRecord.getCurrentStreak()),
                    Map.entry("cleanStreakDays", streakRecord.getCleanStreakDays()),
                    Map.entry("previousStreak", 0),
                    Map.entry("availableShields", streakRecord.getStreakShields()),
                    Map.entry("lastCompletedDate", streakRecord.getLastCompletedDate()),
                    Map.entry("longestStreak", streakRecord.getLongestStreak()),
                    Map.entry("isInRedemptionMode", inRedemption),
                    Map.entry("redemptionDaysCompleted", streakRecord.getRedemptionDaysCompleted()),
                    Map.entry("frozenStreakToRestore", streakRecord.getFrozenStreakToRestore()),
                    Map.entry("message", inRedemption
                            ? String.format("Redemption Challenge: Day %d/%d completed! Keep going to restore your %d-day streak and get the %d days.",
                            streakRecord.getRedemptionDaysCompleted(),
                            REDEMPTION_TARGET_DAYS,
                            streakRecord.getFrozenStreakToRestore(),
                            REDEMPTION_TARGET_DAYS)
                            : "Your streak is secure."
                    )
            );
        }


        // Condition B: Check if they successfully hit their metric totals yesterday
        long totalAttempts = userStreakRepository.countYesterdayTotalClaimsAttempts(userId, userTz, yesterday);
        if (totalAttempts >= AMOUNT_FOR_STREAK) {
            boolean shieldAwardedToday = false;
            boolean redemptionCompletedToday = false;

            // IDEMPOTENCY GUARD: Only increment and save if they haven't already processed today's milestone check
            if (lastCompleted == null || !lastCompleted.equals(today)) {
                int newStreak = streakRecord.getCurrentStreak() + 1;
                streakRecord.setCurrentStreak(newStreak);
                streakRecord.setLastCompletedDate(today);
                streakRecord.setSoftResetAt(null);

                if (inRedemption) {
                    int daysDone = (streakRecord.getRedemptionDaysCompleted() != null ? streakRecord.getRedemptionDaysCompleted() : 0) + 1;
                    if (daysDone >= REDEMPTION_TARGET_DAYS) {
                        newStreak = streakRecord.getFrozenStreakToRestore() + daysDone;
                        streakRecord.setCurrentStreak(newStreak);
                        streakRecord.setLastSaverUsedAt(today);
                        streakRecord.setIsInRedemptionMode(false);
                        streakRecord.setRedemptionDaysCompleted(0);
                        streakRecord.setFrozenStreakToRestore(0);
                        redemptionCompletedToday = true;
                        log.info("User ID {} completed 7-Day Redemption! Restored streak to {}", userId, newStreak);
                    } else {
                        streakRecord.setRedemptionDaysCompleted(daysDone);
                    }
                } else {
                    int newCleanStreak = (streakRecord.getCleanStreakDays() != null ? streakRecord.getCleanStreakDays() : 0) + 1;
                    streakRecord.setCleanStreakDays(newCleanStreak);
                    int currentShields = streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0;
                    if (isShieldMilestone(newCleanStreak) && currentShields < MAX_SHIELDS) {
                        streakRecord.setStreakShields(currentShields + 1);
                        streakRecord.setTotalShieldsEarned(streakRecord.getTotalShieldsEarned() + 1);
                        shieldAwardedToday = true;
                        log.info("User ID {} earned a Streak Shield at {} clean days! Inventory: {}/{}",
                                userId, newCleanStreak, streakRecord.getStreakShields(), MAX_SHIELDS);
                    }
                }

                // 📈 LONGEST STREAK RECORD-BREAKER ENGINE
                if (newStreak > streakRecord.getLongestStreak()) {
                    streakRecord.setLongestStreak(newStreak);
                    log.info("User ID {} just broke their personal record! New Longest Streak: {}", userId, newStreak);
                }
                userStreakRepository.save(streakRecord);
            }
            String successMessage = redemptionCompletedToday
                    ? "7-Day Redemption Challenge complete! Your full streak has been restored!"
                    : (shieldAwardedToday
                       ? "Daily goal achieved! Streak increased and you earned a new Streak Shield!"
                       : "Daily goal achieved! Streak increased.");
            return Map.ofEntries(
                    Map.entry("status", "SAFE"),
                    Map.entry("actionRequired", false),
                    Map.entry("currentStreak", streakRecord.getCurrentStreak()),
                    Map.entry("cleanStreakDays", streakRecord.getCleanStreakDays()),
                    Map.entry("previousStreak", 0),
                    Map.entry("availableShields", streakRecord.getStreakShields()),
                    Map.entry("shieldAwardedToday", shieldAwardedToday),
                    Map.entry("isInRedemptionMode", Boolean.TRUE.equals(streakRecord.getIsInRedemptionMode())),
                    Map.entry("redemptionDaysCompleted", streakRecord.getRedemptionDaysCompleted()),
                    Map.entry("frozenStreakToRestore", streakRecord.getFrozenStreakToRestore()),
                    Map.entry("lastCompletedDate", today),
                    Map.entry("totalAttempts", totalAttempts),
                    Map.entry("longestStreak", streakRecord.getLongestStreak()),
                    Map.entry("message", successMessage)
            );
        }

        // Condition C: Retroactive Recovery Window Check (User accepted reset TODAY)
        if (softResetAt != null && softResetAt.equals(today)) {
            Map<String, Object> saverFlags = buildSaverEligibilityFlags(userId, streakRecord, today, userTz);
            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("status", "STREAK_SOFT_RESET");
            response.put("actionRequired", false);
            response.put("currentStreak", 0);
            response.put("cleanStreakDays", 0);
            response.put("previousStreak", streakRecord.getPreviousStreak() != null ? streakRecord.getPreviousStreak() : 0);
            response.put("availableShields", streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0);
            response.put("saverOnCooldown", saverFlags.get("saverOnCooldown"));
            response.put("saverCooldownDaysRemaining", saverFlags.get("saverCooldownDaysRemaining"));
            response.put("isSaverInstantUnlocked", saverFlags.get("isSaverInstantUnlocked"));
            response.put("canStartRedemption", saverFlags.get("canStartRedemption"));
            response.put("recentConsistentDaysCount", saverFlags.get("recentConsistentDaysCount"));
            response.put("requiredConsistentDays", saverFlags.get("requiredConsistentDays"));
            response.put("lastCompletedDate", lastCompleted); // Exact DB date (never faked as today)
            response.put("totalAttempts", totalAttempts);
            response.put("longestStreak", streakRecord.getLongestStreak() != null ? streakRecord.getLongestStreak() : 0);
            response.put("message", "You accepted a reset today, but you can still recover your streak before midnight!");
            return response;
        }

// Hard Reset (User missed 2 or more days — also fails any active redemption challenge)
        LocalDate twoDaysAgo = today.minusDays(2);
        if (lastCompleted == null || lastCompleted.isBefore(twoDaysAgo)) {
            streakRecord.setCurrentStreak(0);
            streakRecord.setCleanStreakDays(0);
            streakRecord.setSoftResetAt(null);
            streakRecord.setIsInRedemptionMode(false);
            streakRecord.setRedemptionDaysCompleted(0);
            streakRecord.setFrozenStreakToRestore(0);
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
                    "message", inRedemption
                            ? "You missed a day during your 7-Day Redemption Challenge. Your frozen streak was lost."
                            : "Your streak has reset because you missed multiple days."
            );
        }
// If user missed yesterday while ALREADY in a 7-Day Redemption Challenge, fail the challenge immediately
        if (inRedemption) {
            streakRecord.setCurrentStreak(0);
            streakRecord.setCleanStreakDays(0);
            streakRecord.setIsInRedemptionMode(false);
            streakRecord.setRedemptionDaysCompleted(0);
            streakRecord.setFrozenStreakToRestore(0);
            userStreakRepository.save(streakRecord);
            return Map.of(
                    "status", "STREAK_BROKEN",
                    "actionRequired", false,
                    "currentStreak", 0,
                    "cleanStreakDays", 0,
                    "previousStreak", 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", lastCompleted,
                    "totalAttempts", totalAttempts,
                    "longestStreak", streakRecord.getLongestStreak(),
                    "message", "You missed a day during your 7-Day Redemption Challenge. Your frozen streak was lost."
            );
        }
        // Condition D: Action Required (First-time break notification with Dual-Path Saver flags)
        Map<String, Object> saverFlags = buildSaverEligibilityFlags(userId, streakRecord, today, userTz);
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("status", "STREAK_IN_DANGER");
        response.put("actionRequired", true);
        response.put("currentStreak", streakRecord.getCurrentStreak() != null ? streakRecord.getCurrentStreak() : 0);
        response.put("cleanStreakDays", streakRecord.getCleanStreakDays() != null ? streakRecord.getCleanStreakDays() : 0);
        response.put("previousStreak", 0);
        response.put("availableShields", streakRecord.getStreakShields() != null ? streakRecord.getStreakShields() : 0);
        response.put("saverOnCooldown", saverFlags.get("saverOnCooldown"));
        response.put("saverCooldownDaysRemaining", saverFlags.get("saverCooldownDaysRemaining"));
        response.put("isSaverInstantUnlocked", saverFlags.get("isSaverInstantUnlocked"));
        response.put("canStartRedemption", saverFlags.get("canStartRedemption"));
        response.put("recentConsistentDaysCount", saverFlags.get("recentConsistentDaysCount"));
        response.put("requiredConsistentDays", saverFlags.get("requiredConsistentDays"));
        response.put("lastCompletedDate", lastCompleted); // Exact DB date
        response.put("totalAttempts", totalAttempts);
        response.put("longestStreak", streakRecord.getLongestStreak() != null ? streakRecord.getLongestStreak() : 0);
        response.put("message", "You did not complete your 10 ad claims yesterday.");
        return response;

    }

    /**
     * Executes the user's manual option choice from the app pop-up interface.
     * Strictly hardened against repeat-execution exploits on the same calendar day.
     */
    @Transactional
    public void executeStreakRescueSelection(Long userId, String selectedOption) {
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));
        String userTz = resolveUserTimezone(streakRecord);
        LocalDate today = LocalDate.now(ZoneId.of(userTz));
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
                validateSaverCooldown(streakRecord, today);
                int consistentDays = countPriorConsistentDays(userId, today,userTz);
                if (consistentDays < REQUIRED_CONSISTENT_DAYS_FOR_SAVER) {
                    throw new IllegalStateException("Instant Streak Saver requires 7 consecutive days of 10+ claims prior to missing a day. Choose START_SAVER_REDEMPTION instead.");
                }
                userStreakRepository.consumePremiumVideoSaver(userId, targetStreakValue);
                // Restores currentStreak, clears soft_reset_at, locks today, and resets clean_streak_days to 0
                userStreakRepository.restoreStreakState(userId, targetStreakValue, today);
                userStreakRepository.updateLastSaverUsedAt(userId, today);
                break;
            case "START_SAVER_REDEMPTION":
                validateSaverCooldown(streakRecord, today);
                if (targetStreakValue < 1) {
                    throw new IllegalStateException("No active streak to freeze for redemption.");
                }
                streakRecord.setFrozenStreakToRestore(targetStreakValue);
                streakRecord.setCurrentStreak(0);
                streakRecord.setCleanStreakDays(0);
                streakRecord.setPreviousStreak(0);
                streakRecord.setSoftResetAt(null);
                streakRecord.setIsInRedemptionMode(true);
                streakRecord.setRedemptionDaysCompleted(0);
                streakRecord.setLastCompletedDate(today);
                userStreakRepository.save(streakRecord);
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
    private Map<String, Object> buildSaverEligibilityFlags(Long userId, UserStreak streak, LocalDate today, String userTz) {
        long cooldownRemaining = 0;
        if (streak.getLastSaverUsedAt() != null) {
            long daysSince = ChronoUnit.DAYS.between(streak.getLastSaverUsedAt(), today);
            cooldownRemaining = Math.max(0, SAVER_COOLDOWN_DAYS - daysSince);
        }
        boolean onCooldown = cooldownRemaining > 0;
        int consistentDays = countPriorConsistentDays(userId, today, userTz);

        return Map.of(
                "saverOnCooldown", onCooldown,
                "saverCooldownDaysRemaining", cooldownRemaining,
                "isSaverInstantUnlocked", !onCooldown && consistentDays >= REQUIRED_CONSISTENT_DAYS_FOR_SAVER,
                "canStartRedemption", !onCooldown && consistentDays < REQUIRED_CONSISTENT_DAYS_FOR_SAVER,
                "recentConsistentDaysCount", consistentDays,
                "requiredConsistentDays", REQUIRED_CONSISTENT_DAYS_FOR_SAVER
        );
    }

    private void validateSaverCooldown(UserStreak streak, LocalDate today) {
        if (streak.getLastSaverUsedAt() != null) {
            long daysSince = ChronoUnit.DAYS.between(streak.getLastSaverUsedAt(), today);
            if (daysSince < SAVER_COOLDOWN_DAYS) {
                throw new IllegalStateException("Streak Saver is on cooldown for " + (SAVER_COOLDOWN_DAYS - daysSince) + " more day(s).");
            }
        }
    }
    private int countPriorWindowClaims(Long userId, LocalDate lastCompleted, LocalDate today) {
        LocalDate windowEnd = (lastCompleted != null && !lastCompleted.equals(today)) ? lastCompleted : today.minusDays(2);
        LocalDate windowStart = windowEnd.minusDays(CLAIM_WINDOW_DAYS - 1);
        return contributionRepository.countClaimsBetweenDates(userId, windowStart, windowEnd);
    }


    private int countPriorConsistentDays(Long userId, LocalDate today, String userTz) {
        LocalDate windowEnd = today.minusDays(2);
        LocalDate windowStart = windowEnd.minusDays(REQUIRED_CONSISTENT_DAYS_FOR_SAVER - 1);
        return contributionRepository.countQualifyingDaysBetweenDates(
                userId, userTz, windowStart, windowEnd, AMOUNT_FOR_STREAK
        );
    }


    private boolean isShieldMilestone(int cleanDays) {
        return cleanDays > 0 && (MILESTONE_SET.contains(cleanDays) || (cleanDays > 100 && cleanDays % 100 == 0));
    }

    private String resolveUserTimezone(UserStreak streakRecord) {
        if (streakRecord.getUser() != null
                && streakRecord.getUser().getTimezone() != null
                && !streakRecord.getUser().getTimezone().isBlank()) {
            try {
                ZoneId.of(streakRecord.getUser().getTimezone()); // Validate IANA timezone
                return streakRecord.getUser().getTimezone();
            } catch (Exception ignored) {
            }
        }
        return "America/New_York";
    }

}