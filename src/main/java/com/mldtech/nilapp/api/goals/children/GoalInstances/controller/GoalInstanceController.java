package com.mldtech.nilapp.api.goals.children.GoalInstances.controller;

import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.goals.children.GoalInstances.service.GoalInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goal-instances")
@RequiredArgsConstructor
public class GoalInstanceController {

    private final GoalInstanceService service;

    @GetMapping
    public Object getAll() {
        return service.getAll();
    }

    @PostMapping("/{goalId}")
    public Object create(@PathVariable Long goalId, @RequestBody GoalInstance instance) {
        return service.create(goalId, instance);
    }
}
