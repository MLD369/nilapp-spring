package com.mldtech.nilapp.api.group.children.GroupAchievement.service;

import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
import com.mldtech.nilapp.api.group.children.GroupAchievement.repository.GroupAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupAchievementService {

    private final GroupAchievementRepository repository;
    private final GroupRepository groupRepository;
    private final AchievementRepository achievementRepository;

    public List<GroupAchievement> getAchievementsForGroup(Long groupId) {
        return repository.findByGroup_GroupId(groupId);
    }

    public GroupAchievement addGroupAchievement(Long groupId, Long achievementId) {

        if (repository.existsByGroup_GroupIdAndAchievement_AchievementId(groupId, achievementId)) {
            return repository.findByGroup_GroupId(groupId).stream()
                    .filter(ga -> ga.getAchievement().getAchievementId().equals(achievementId))
                    .findFirst()
                    .orElseThrow();
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        GroupAchievement ga = GroupAchievement.builder()
                .group(group)
                .achievement(achievement)
                .earnedAt(LocalDate.now())
                .build();

        return repository.save(ga);
    }

    public void removeGroupAchievement(Long groupId, Long achievementId) {
        repository.deleteByGroup_GroupIdAndAchievement_AchievementId(groupId, achievementId);
    }
}
