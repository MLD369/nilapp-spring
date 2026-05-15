package com.mldtech.nilapp.api.entities.children.EntityAchievement.service;

import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
import com.mldtech.nilapp.api.entities.children.EntityAchievement.model.EntityAchievement;
import com.mldtech.nilapp.api.entities.children.EntityAchievement.repository.EntityAchievementRepository;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityAchievementService {

    private final EntityAchievementRepository repository;
    private final EntityRepository entityRepository;
    private final AchievementRepository achievementRepository;

    public List<EntityAchievement> getAchievementsForEntity(Long entityId) {
        return repository.findByEntity_EntityId(entityId);
    }

    public EntityAchievement addAchievementToEntity(Long entityId, Long achievementId) {

        if (repository.existsByEntity_EntityIdAndAchievement_AchievementId(entityId, achievementId)) {
            throw new RuntimeException("Achievement already assigned to entity");
        }

        Entities entity = entityRepository.findById(entityId)
                .orElseThrow(() -> new RuntimeException("Entity not found"));

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        EntityAchievement ea = EntityAchievement.builder()
                .entity(entity)
                .achievement(achievement)
                .earnedAt(LocalDate.now())
                .build();

        return repository.save(ea);
    }

    public void removeAchievementFromEntity(Long entityId, Long achievementId) {
        repository.deleteByEntity_EntityIdAndAchievement_AchievementId(entityId, achievementId);
    }
}
