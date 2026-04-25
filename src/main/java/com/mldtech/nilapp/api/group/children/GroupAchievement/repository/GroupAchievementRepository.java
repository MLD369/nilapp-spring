//package com.mldtech.nilapp.api.group.children.GroupAchievement.repository;
//
//import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface GroupAchievementRepository extends JpaRepository<GroupAchievement, GroupAchievementId> {
//
//    List<GroupAchievement> findByIdGroupId(Long groupId);
//
//    List<GroupAchievement> findByIdAchievementId(Long achievementId);
//
//    boolean existsByIdGroupIdAndIdAchievementId(Long groupId, Long achievementId);
//
//    void deleteByIdGroupIdAndIdAchievementId(Long groupId, Long achievementId);
//}
