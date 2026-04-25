package com.mldtech.nilapp.api.users.children.UserGoal.repository;


import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    List<UserGoal> findByUserUserIdAndIsActiveTrue(Long userId);
}
