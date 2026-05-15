package com.mldtech.nilapp.api.users.dto;

//import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String email;
    private Long totalLifetimeCoins;

    private List<UserEntity> entities;
    private List<UserGroup> groups;
    private List<UserAchievement> achievements;

    private List<UserGoal> goals = new ArrayList<>();
//    private List<UserGoalHistory> goalHistory;
    private List<Contribution> contributions;
    private List<DailyStat> dailyStats;
    private List<Friend> friends;

//    public UserProfileResponse(User user){
//        this.username = user.getUsername();
//        this.email = user.getEmail();
//        this.userId = user.getUserId();
//
//
//    }
}

