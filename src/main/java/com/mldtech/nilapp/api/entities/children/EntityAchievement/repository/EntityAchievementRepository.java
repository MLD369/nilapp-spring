package com.mldtech.nilapp.api.entities.children.EntityAchievement.repository;

import com.mldtech.nilapp.api.entities.children.EntityAchievement.model.EntityAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntityAchievementRepository extends JpaRepository<EntityAchievement, Long> {

    List<EntityAchievement> findByEntity_EntityId(Long entityId);

    List<EntityAchievement> findByAchievement_AchievementId(Long achievementId);

    boolean existsByEntity_EntityIdAndAchievement_AchievementId(Long entityId, Long achievementId);

    void deleteByEntity_EntityIdAndAchievement_AchievementId(Long entityId, Long achievementId);
}
