package com.mldtech.nilapp.api.users.children.UserProgress.controller;

import com.mldtech.nilapp.api.users.children.UserProgress.model.UserProgress;
import com.mldtech.nilapp.api.users.children.UserProgress.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService service;

    @GetMapping
    public Object getAll() {
        return service.getAll();
    }

    @PostMapping("/{userGoalId}")
    public Object addProgress(
            @PathVariable Long userGoalId,
            @RequestBody UserProgress progress
    ) {
        return service.addProgress(userGoalId, progress);
    }
}

