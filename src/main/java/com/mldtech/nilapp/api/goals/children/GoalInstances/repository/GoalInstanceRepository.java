package com.mldtech.nilapp.api.goals.children.GoalInstances.repository;


import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalInstanceRepository extends JpaRepository<GoalInstance, Long> {
}

