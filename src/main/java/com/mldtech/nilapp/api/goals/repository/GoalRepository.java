package com.mldtech.nilapp.api.goals.repository;

import com.mldtech.nilapp.api.goals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByEntityId(Long entityId);

    List<Goal> findByAchievementId(Long achievementId);

    List<Goal> findByCompletedDateIsNull();

    List<Goal> findByCompletedDateIsNotNull();
}
