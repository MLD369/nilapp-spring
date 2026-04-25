//package com.mldtech.nilapp.api.group.children.GroupAchievement.service;
//
//import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
//import com.mldtech.nilapp.api.group.children.GroupAchievement.repository.GroupAchievementRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class GroupAchievementService {
//
//    private final GroupAchievementRepository repository;
//
//    public List<GroupAchievement> getAchievementsForGroup(Long groupId) {
//        return repository.findByIdGroupId(groupId);
//    }
//
//    public GroupAchievement addGroupAchievement(Long groupId, Long achievementId) {
//
//        GroupAchievementId id = new GroupAchievementId(groupId, achievementId);
//
//        if (repository.existsByIdGroupIdAndIdAchievementId(groupId, achievementId)) {
//            return repository.findById(id).orElseThrow();
//        }
//
//        GroupAchievement ga = GroupAchievement.builder()
//                .id(id)
//                .earnedAt(LocalDate.now())
//                .build();
//
//        return repository.save(ga);
//    }
//
//    public void removeGroupAchievement(Long groupId, Long achievementId) {
//        repository.deleteByIdGroupIdAndIdAchievementId(groupId, achievementId);
//    }
//}
