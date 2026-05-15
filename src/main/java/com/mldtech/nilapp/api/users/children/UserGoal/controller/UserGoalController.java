package com.mldtech.nilapp.api.users.children.UserGoal.controller;

import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoal.service.UserGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
        return service.joinGoal(userId, instanceId, userGoal);
    }

}
