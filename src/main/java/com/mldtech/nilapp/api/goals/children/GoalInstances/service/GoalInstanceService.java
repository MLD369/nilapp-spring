package com.mldtech.nilapp.api.goals.children.GoalInstances.service;

import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.goals.children.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalInstanceService {

    private final GoalInstanceRepository repo;
    private final GoalRepository goalRepo;

    public CustomResponse<?> create(Long goalId, GoalInstance instance) {
        instance.setGoal(goalRepo.getReferenceById(goalId));
        return new CustomResponse<>(repo.save(instance), HttpStatus.OK, "200");
    }

    public CustomResponse<?> getAll() {
        return new CustomResponse<>(repo.findAll(), HttpStatus.OK, "200");
    }
}
