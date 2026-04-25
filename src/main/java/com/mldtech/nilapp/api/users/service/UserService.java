package com.mldtech.nilapp.api.users.service;


//import com.mldtech.nilapp.api.friend.model.Friend;
//import com.mldtech.nilapp.api.friend.repository.FriendRepository;
//import com.mldtech.nilapp.api.users.children.UserAchievement.repository.UserAchievementRepository;
//import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
//import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.repository.UserGoalHistoryRepository;
//import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
//import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    private final UserEntityRepository userEntityRepository;
//    private final UserGroupRepository userGroupRepository;
//    private final UserAchievementRepository userAchievementRepository;
//    private final UserGoalRepository userGoalRepository;
//    private final UserGoalHistoryRepository userGoalHistoryRepository;
//    private final FriendRepository friendRepository;

    public UserProfileResponse getUserProfile(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        var entities = userEntityRepository.findByUserEntitiesByUserId(userId)
//                .stream()
//                .map(ue -> ue.getEntity().getEntityId())
//                .toList();
//
//        var groups = userGroupRepository.findByUserGroupUserId(userId)
//                .stream()
//                .map(ug -> ug.getGroup().getGroupId())
//                .toList();
//
//        var achievements = userAchievementRepository.findByUserUserId(userId)
//                .stream()
//                .map(ua -> ua.getAchievement().getAchievementId())
//                .toList();
//
//        var activeGoals = userGoalRepository.findByUserUserIdAndIsActiveTrue(userId);
//
//        var goalHistory = userGoalHistoryRepository.findByUserUserId(userId);
//
//        var friends = friendRepository.findByFriendUserId(userId)
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .entityIds(entities)
//                .stream()
//                .map(Friend::getId)
//                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .totalLifetimeCoins(user.getTotalLifetimeCoins())
                .groups(user.getUserGroups().stream().toList())
                .achievements(user.getUserAchievements().stream().toList())
                .goals(user.getUserGoals().stream().toList())
                .goalHistory(user.getUserGoalHistories().stream().toList())
                .entities(user.getUserEntities().stream().toList())
                .contributions(user.getContributions().stream().toList())
                .dailyStats(user.getDailyStats().stream().toList())
//                .friends(user.getUserFriends().stream().toList())
                .build();
    }

}

