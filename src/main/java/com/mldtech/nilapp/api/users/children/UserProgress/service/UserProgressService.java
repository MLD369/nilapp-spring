package com.mldtech.nilapp.api.users.children.UserProgress.service;

import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.children.UserProgress.model.UserProgress;
import com.mldtech.nilapp.api.users.children.UserProgress.repository.UserProgressRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository repo;
    private final UserGoalRepository userGoalRepo;

    public CustomResponse<?> addProgress(Long userGoalId, UserProgress progress) {
        progress.setUserGoal(userGoalRepo.getReferenceById(userGoalId));
        return new CustomResponse<>(repo.save(progress), HttpStatus.OK, "200");
    }

    public CustomResponse<?> getAll() {
        return new CustomResponse<>(repo.findAll(), HttpStatus.OK, "200");
    }
}

