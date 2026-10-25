package com.mldtech.nilapp.api.users.children.UserStreak.controller;


import com.mldtech.nilapp.api.users.children.UserStreak.service.UserStreakService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user-streaks")
@AllArgsConstructor
public class UserStreakController {




    private final UserStreakService userStreakService;

    /**
     * Endpoint triggered instantly when the user opens the mobile application.
     * Evaluates if their streak is safe or if the front-end should lock the screen and force a rescue pop-up.
     */
    @GetMapping("/{userId}/check-status")
    public ResponseEntity<Map<String, Object>> verifyStreakOnAppOpen(@PathVariable("userId") Long userId) {

        Map<String, Object> streakStatus = userStreakService.checkStreakSafetyOnAppOpen(userId);

        return ResponseEntity.ok(streakStatus);
    }

    /**
     * Endpoint triggered after the user selects a choice on the Rescue Pop-up screen
     * and successfully finishes viewing the corresponding ad type.
     */
    @PostMapping("/{userId}/rescue-selection")
    public ResponseEntity<Map<String, Object>> submitStreakRescueChoice(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> payload) {

        // Securely extract the choice string from the JSON body payload
        String selectedOption = payload.get("selectedOption").toString(); // USE_SHIELD_SHORT_AD, WATCH_PREMIUM_LONG_AD, ACCEPT_RESET

        try {
            userStreakService.executeStreakRescueSelection(userId, selectedOption);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "User streak state has been safely updated.",
                    "appliedOption", selectedOption
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }
}
