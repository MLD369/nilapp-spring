package com.mldtech.nilapp.api.AchievementType.repository;

import com.mldtech.nilapp.api.AchievementType.model.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementTypeRepository extends JpaRepository<AchievementType, Integer> {

    boolean existsByType(String type);
}
