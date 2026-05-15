package com.mldtech.nilapp.api.group.children.GroupAchievement.repository;

import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupAchievementRepository extends JpaRepository<GroupAchievement, Long> {

    List<GroupAchievement> findByGroup_GroupId(Long groupId);

    List<GroupAchievement> findByAchievement_AchievementId(Long achievementId);

    boolean existsByGroup_GroupIdAndAchievement_AchievementId(Long groupId, Long achievementId);

    void deleteByGroup_GroupIdAndAchievement_AchievementId(Long groupId, Long achievementId);
}
