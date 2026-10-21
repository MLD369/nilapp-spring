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

    private final UserGoalService service;

    @GetMapping
    public Object getAll() {
        return service.getAll();
    }

    @PostMapping("/{userId}/{instanceId}")
    public Object joinGoal(
            @PathVariable Long userId,
            @PathVariable Long instanceId,
            @RequestBody UserGoal userGoal
    ) {
        return service.joinGoal(userId, instanceId);
    }

    /**
     * Endpoint hit manually when a user clicks "Start This Challenge Again" on their phone UI.
     */
    // TODO customresponse
    @PostMapping("/{userId}/rejoin-challenge")
    public ResponseEntity<Map<String, Object>> userRequestedGoalRejoin(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> payload) {

        Long goalInstanceId = payload.get("goalInstanceId") != null ? Long.valueOf(payload.get("goalInstanceId").toString()) : null;
        String targetType = payload.get("targetType").toString(); // 'COINS' or 'STEPS'
        int targetAmount = Integer.parseInt(payload.get("targetAmount").toString());
        double allocationPct = Double.parseDouble(payload.get("allocationPct").toString());

        try {
            service.rejoinGoalChallenge(userId, goalInstanceId, targetType, targetAmount, allocationPct);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Fresh challenge timeline initialized successfully back at zero progress."
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ALREADY_ACTIVE",
                    "message", e.getMessage().replace("ALREADY_ACTIVE: ", "")
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "An unexpected error occurred while rejoining the challenge."
            ));
        }
    }



}
