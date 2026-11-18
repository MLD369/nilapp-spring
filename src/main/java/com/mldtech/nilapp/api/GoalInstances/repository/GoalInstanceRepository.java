package com.mldtech.nilapp.api.GoalInstances.repository;


import com.mldtech.nilapp.api.GoalInstances.model.GoalInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GoalInstanceRepository extends JpaRepository<GoalInstance, Long> {
    List<GoalInstance> findByEntity_EntityId(Long entityId);
    List<GoalInstance> findByGroup_GroupId(Long groupId);
//    List<GoalInstance> findByUser_UserId(Long userId); // if user goals exist
    @Query("SELECT gi FROM GoalInstance gi WHERE gi.isActive = true AND gi.endDate IS NOT NULL AND gi.endDate < :today")
    List<GoalInstance> findExpiredActiveInstances(@Param("today") LocalDate today);

    @Query("""
    SELECT CASE WHEN COUNT(gi) > 0 THEN true ELSE false END
    FROM GoalInstance gi
    WHERE gi.goalInstanceId = :goalInstanceId
      AND gi.isActive = true
      AND (gi.isComplete IS NULL OR gi.isComplete = false)
      AND (gi.endDate IS NULL OR gi.endDate >= :today)
""")
    boolean isGoalInstanceOpen(@Param("goalInstanceId") Long goalInstanceId, @Param("today") LocalDate today);


}

