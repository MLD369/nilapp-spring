package com.mldtech.nilapp.api.GoalTypes.service;

import com.mldtech.nilapp.api.GoalTypes.model.GoalType;
import com.mldtech.nilapp.api.GoalTypes.repository.GoalTypeRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalTypeService {

    private final GoalTypeRepository repo;

    public CustomResponse<?> getAll() {
        return new CustomResponse<>(repo.findAll(), HttpStatus.OK, "200");
    }

    public CustomResponse<?> create(GoalType type) {
        return new CustomResponse<>(repo.save(type), HttpStatus.OK, "200");
    }
}

