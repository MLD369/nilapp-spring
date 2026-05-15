package com.mldtech.nilapp.api.goals.children.GoalTypes.repository;

import com.mldtech.nilapp.api.goals.children.GoalTypes.model.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalTypeRepository extends JpaRepository<GoalType, Integer> {
}

