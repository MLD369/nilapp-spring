package com.mldtech.nilapp.api.GoalTypes.repository;

import com.mldtech.nilapp.api.GoalTypes.model.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalTypeRepository extends JpaRepository<GoalType, Integer> {
}

