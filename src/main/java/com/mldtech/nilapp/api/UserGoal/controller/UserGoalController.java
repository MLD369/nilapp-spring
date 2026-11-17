package com.mldtech.nilapp.api.UserGoal.controller;

import com.mldtech.nilapp.api.UserGoal.service.UserGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-goals")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;

    @GetMapping
    public Object getAll() {
        return userGoalService.getAll();
    }


}
