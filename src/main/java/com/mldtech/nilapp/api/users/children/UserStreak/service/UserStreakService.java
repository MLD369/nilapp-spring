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
     * Looks up all state historical data directly from the DB using only the userId.
     */
    @Transactional
    public Map<String, Object> checkStreakSafetyOnAppOpen(Long userId) {
        // 1. Load the user's current streak configuration row from the DB
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastCompleted = streakRecord.getLastCompletedDate();

        // 2. Safe Condition A: User already secured or rescued their streak today or yesterday
        if (lastCompleted != null && (lastCompleted.equals(today) || lastCompleted.equals(yesterday))) {
            return Map.of(
                    "status", "SAFE",
                    "actionRequired", false,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "previousStreak", streakRecord.getPreviousStreak() != null ? streakRecord.getPreviousStreak() : 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", streakRecord.getLastCompletedDate(),
                    "message", "Your streak is secure."
            );
        }

        // 3. Safe Condition B: Check combined total attempts yesterday (Pending + Approved)
        long totalAttempts = userStreakRepository.countYesterdayTotalClaimsAttempts(userId);
        if (totalAttempts >= amountForStreak) {
            // IDEMPOTENCY GUARD: Only increment and save if they haven't already processed today's milestone check
            if (lastCompleted == null || !lastCompleted.equals(today)) {
                streakRecord.setCurrentStreak(streakRecord.getCurrentStreak() + 1);
                streakRecord.setLastCompletedDate(today);
                userStreakRepository.save(streakRecord); // Persist updated metrics cleanly
            }

            return Map.of(
                    "status", "SAFE",
                    "actionRequired", false,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "previousStreak", streakRecord.getPreviousStreak() != null ? streakRecord.getPreviousStreak() : 0,
                    "availableShields", streakRecord.getStreakShields(),
                    "lastCompletedDate", today,
                    "totalAttempts", totalAttempts,
                    "message", "You completed your 10 ad claims yesterday! Streak increased."
            );
        }

        // 4. Action Required: User failed criteria. Return details to populate front-end modal.
        // Keeps 'actionRequired' true across multiple app opens until a rescue choice is executed.
        return Map.of(
                "status", "STREAK_IN_DANGER",
                "actionRequired", true,
                "currentStreak", streakRecord.getCurrentStreak(),
                "previousStreak", streakRecord.getPreviousStreak() != null ? streakRecord.getPreviousStreak() : 0,
                "availableShields", streakRecord.getStreakShields(),
                "lastCompletedDate", lastCompleted != null ? lastCompleted : yesterday.minusDays(1),
                "totalAttempts", totalAttempts,
                "message", "You did not complete your 10 ad claims yesterday. Quick, save your streak!"
        );
    }

    /**
     * Executes the user's manual option choice from the app pop-up interface.
     * Fetches current state metrics dynamically to prevent client-side data falsification.
     */
    @Transactional
    public void executeStreakRescueSelection(Long userId, String selectedOption) {
        // 1. Fetch the user's record to pull their current active streak value
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        // 2. Direct evaluation tree mapping straight to database operations
        switch (selectedOption) {
            case "USE_SHIELD_SHORT_AD":
                int rowsUpdated = userStreakRepository.consumeShieldWithShortAd(userId);
                if (rowsUpdated == 0) {
                    throw new RuntimeException("Action failed. User does not have available shields in inventory.");
                }
                // Freeze date to today to take them out of the danger check loop
                userStreakRepository.updateLastCompletedDate(userId, LocalDate.now());
                break;

            case "WATCH_PREMIUM_LONG_AD":
                userStreakRepository.consumePremiumVideoSaver(userId, streakRecord.getCurrentStreak());
                // Freeze date to today to take them out of the danger check loop
                userStreakRepository.updateLastCompletedDate(userId, LocalDate.now());
                break;

            case "ACCEPT_RESET":
                // FIXED: Back up current streak count into previous_streak column before zeroing out the profile row
                userStreakRepository.archiveAndResetActiveStreak(userId, streakRecord.getCurrentStreak(), LocalDate.now());
                break;

            default:
                throw new IllegalArgumentException("Unknown rescue selection option pattern: " + selectedOption);
        }
    }
}