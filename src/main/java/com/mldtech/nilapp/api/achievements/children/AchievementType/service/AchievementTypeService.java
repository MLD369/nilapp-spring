//package com.mldtech.nilapp.api.achievements.children.AchievementType.service;
//
//import com.mldtech.nilapp.api.achievements.children.AchievementType.model.AchievementType;
//import com.mldtech.nilapp.api.achievements.children.AchievementType.repository.AchievementTypeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AchievementTypeService {
//
//    private final AchievementTypeRepository repository;
//
//    public List<AchievementType> getAllTypes() {
//        return repository.findAll();
//    }
//
//    public AchievementType getType(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Achievement type not found"));
//    }
//
//    public AchievementType createType(AchievementType type) {
//        return repository.save(type);
//    }
//
//    public AchievementType updateType(Long id, AchievementType updated) {
//        AchievementType existing = getType(id);
//        existing.setType(updated.getType());
//        existing.setMeaning(updated.getMeaning());
//        return repository.save(existing);
//    }
//
//    public void deleteType(Long id) {
//        repository.deleteById(id);
//    }
//}
//
