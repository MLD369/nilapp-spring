package com.mldtech.nilapp.api.goals.controller;

import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.goals.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService service;

    @GetMapping
    public List<Goal> getAllGoals() {
        return service.getAllGoals();
    }

    @GetMapping("/{goalId}")
    public Goal getGoal(@PathVariable Long goalId) {
        return service.getGoal(goalId);
    }

    @GetMapping("/entity/{entityId}")
    public List<Goal> getGoalsForEntity(@PathVariable Long entityId) {
        return service.getGoalsForEntity(entityId);
    }

    @PostMapping
    public Goal createGoal(@RequestBody Goal goal) {
        return service.createGoal(goal);
    }

    @PutMapping("/{goalId}")
    public Goal updateGoal(@PathVariable Long goalId, @RequestBody Goal updated) {
        return service.updateGoal(goalId, updated);
    }

    @PutMapping("/{goalId}/complete")
    public Goal completeGoal(@PathVariable Long goalId) {
        return service.completeGoal(goalId);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable Long goalId) {
        service.deleteGoal(goalId);
    }
}

