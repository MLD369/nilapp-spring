package com.mldtech.nilapp.api.users.children.UserGoal.controller;

import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoal.service.UserGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user-goals")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;

    @GetMapping
    public Object getAll() {
        return userGoalService.getAll();
    }

    /**
     * Endpoint hit when a user clicks to start an individual challenge or restart a completed one.
     */
    @PostMapping("/{userId}/join/{instanceId}")
    public ResponseEntity<?> joinChallengeInstance(
            @PathVariable("userId") Long userId,
            @PathVariable("instanceId") Long instanceId) {
        return ResponseEntity.ok(userGoalService.joinGoal(userId, instanceId));
    }

    /**
     * Endpoint hit when a user explicitly abandons an active challenge early.
     */
    @PostMapping("/{userId}/leave/{instanceId}")
    public ResponseEntity<?> leaveChallengeInstance(
            @PathVariable("userId") Long userId,
            @PathVariable("instanceId") Long instanceId) {
        return ResponseEntity.ok(userGoalService.leaveGoal(userId, instanceId));
    }
}
