package com.mldtech.nilapp.api.achievements.service;

import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository repository;

    public List<Achievement> getAllAchievements() {
        return repository.findAll();
    }

    public Achievement getAchievement(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
    }

    public List<Achievement> getAchievementsByType(Long typeId) {
        return repository.findByAchievementType_AchievementTypeId(typeId);
    }

    public Achievement createAchievement(Achievement achievement) {

        if (repository.existsByAchievement(achievement.getAchievement())) {
            throw new RuntimeException("Achievement already exists");
        }

        return repository.save(achievement);
    }

    public Achievement updateAchievement(Long id, Achievement updated) {
        Achievement existing = getAchievement(id);

        existing.setAchievement(updated.getAchievement());
        existing.setDescription(updated.getDescription());
        existing.setBadge(updated.getBadge());
        existing.setRequiredSteps(updated.getRequiredSteps());
        existing.setRequiredDistance(updated.getRequiredDistance());
        existing.setAchievementType(updated.getAchievementType());

        return repository.save(existing);
    }

    public void deleteAchievement(Long id) {
        repository.deleteById(id);
    }
}
