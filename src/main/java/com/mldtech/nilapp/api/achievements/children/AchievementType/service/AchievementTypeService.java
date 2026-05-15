package com.mldtech.nilapp.api.achievements.children.AchievementType.service;

import com.mldtech.nilapp.api.achievements.children.AchievementType.model.AchievementType;
import com.mldtech.nilapp.api.achievements.children.AchievementType.repository.AchievementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementTypeService {

    private final AchievementTypeRepository repository;

    public List<AchievementType> getAllAchievementTypes() {
        return repository.findAll();
    }

    public AchievementType getAchievementType(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AchievementType not found"));
    }

    public AchievementType createAchievementType(AchievementType type) {

        if (repository.existsByType(type.getType())) {
            throw new RuntimeException("AchievementType already exists");
        }

        return repository.save(type);
    }

    public AchievementType updateAchievementType(Integer id, AchievementType updated) {
        AchievementType existing = getAchievementType(id);

        existing.setType(updated.getType());
        existing.setMeaning(updated.getMeaning());

        return repository.save(existing);
    }

    public void deleteAchievementType(Integer id) {
        repository.deleteById(id);
    }
}
