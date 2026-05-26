package com.mldtech.nilapp.api.goals.children.GoalInstances.repository;


import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalInstanceRepository extends JpaRepository<GoalInstance, Long> {
    List<GoalInstance> findByEntity_EntityId(Long entityId);
    List<GoalInstance> findByGroup_GroupId(Long groupId);
//    List<GoalInstance> findByUser_UserId(Long userId); // if user goals exist

}

