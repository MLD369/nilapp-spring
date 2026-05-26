package com.mldtech.nilapp.api.goals.service;

import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository repository;

    public List<Goal> getAllGoals() {
        return repository.findAll();
    }

    public Goal getGoal(Long goalId) {
        return repository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

//    public List<Goal> getGoalsForEntity(Long entityId) {
//        return repository.findByEntityId(entityId);
//    }

    public Goal createGoal(Goal goal) {
        return repository.save(goal);
    }

    public Goal updateGoal(Long goalId, Goal updated) {
        Goal existing = getGoal(goalId);

        // FIX: update the Achievement relationship properly
        if (updated.getAchievement() != null) {
            existing.setAchievement(updated.getAchievement());
        }

        existing.setGoal(updated.getGoal());
        existing.setDescription(updated.getDescription());
        existing.setGoalLockDate(updated.getGoalLockDate());
        existing.setCompletedDate(updated.getCompletedDate());

        return repository.save(existing);
    }


    public Goal completeGoal(Long goalId) {
        Goal goal = getGoal(goalId);
        goal.setCompletedDate(LocalDateTime.now());
        return repository.save(goal);
    }

    public void deleteGoal(Long goalId) {
        repository.deleteById(goalId);
    }

    public CustomResponse<?> getAll() {
        return new CustomResponse<>(repository.findAll(), HttpStatus.OK, "200");
    }

    public CustomResponse<?> create(Goal goal) {
        return new CustomResponse<>(repository.save(goal), HttpStatus.OK, "200");
    }
}

