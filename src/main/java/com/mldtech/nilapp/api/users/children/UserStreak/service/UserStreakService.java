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
            return Map.of("status", "SAFE",
                    "actionRequired", false,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "availableShields", streakRecord.getStreakShields());
        }

        // 3. Safe Condition B: Check combined total attempts yesterday (Pending + Approved)
        long totalAttempts = userStreakRepository.countYesterdayTotalClaimsAttempts(userId);
        if (totalAttempts >= 10) {
            return Map.of("status", "SAFE",
                    "actionRequired", false,
                    "totalAttempts",totalAttempts,
                    "currentStreak", streakRecord.getCurrentStreak(),
                    "availableShields", streakRecord.getStreakShields());
        }

        // 4. Action Required: User failed criteria. Return details to populate front-end modal.
        return Map.of(
                "status", "STREAK_IN_DANGER",
                "actionRequired", true,
                "currentStreak", streakRecord.getCurrentStreak(),
                "availableShields", streakRecord.getStreakShields(),
                "message", "You did not complete your 10 ad claims yesterday.",
                "totalAttempts",totalAttempts
        );
    }

    /**
     * Executes the user's manual option choice from the app pop-up interface.
     * Fetches current state metrics dynamically to prevent client-side data falsification.
     */
    @Transactional
    public void executeStreakRescueSelection(Long userId, String selectedOption) {
        // 1. Fetch the user's record to pull their current active streak value [1]
        UserStreak streakRecord = userStreakRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Streak profile not found for user: " + userId));

        // 2. Direct evaluation tree mapping straight to database operations
        switch (selectedOption) {
            case "USE_SHIELD_SHORT_AD":
                // Consumes 1 shield, increments total_shields_used, freezes current_streak number
                int rowsUpdated = userStreakRepository.consumeShieldWithShortAd(userId);
                if (rowsUpdated == 0) {
                    throw new RuntimeException("Action failed. User does not have available shields in inventory.");
                }
                break;

            case "WATCH_PREMIUM_LONG_AD":
                // Premium Video Offer: Increments total_video_savers_used, preserves active streak count
                userStreakRepository.consumePremiumVideoSaver(userId, streakRecord.getCurrentStreak());
                break;

            case "ACCEPT_RESET":
                // Drops current streak count to 0, sets last completed date to today to prevent loop prompting
                userStreakRepository.resolveManualStreakState(userId, 0, LocalDate.now());
                break;

            default:
                throw new IllegalArgumentException("Unknown rescue selection option pattern: " + selectedOption);
        }
    }
}
