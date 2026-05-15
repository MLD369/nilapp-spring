package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserProfileResponse {

    private Long userId;
    private String username;
    private String email;
    private Long totalLifetimeCoins;

    private List<GroupDTO> groups;
    private List<AchievementDTO> achievements;
    private List<GoalDTO> goals;
    private List<EntityDTO> entities;
    private List<ContributionDTO> contributions;
    private List<DailyStatDTO> dailyStats;
    private List<FriendDTO> friends;
}

