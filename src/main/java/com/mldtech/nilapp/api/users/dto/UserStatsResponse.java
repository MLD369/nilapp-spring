package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserStatsResponse {
    private Long userId;
    private String username;
    private Long totalSteps;
    private Long totalCoins;
    private List<GoalDTO> activeGoals;
    private List<GoalDTO> completedGoals;
    private List<AchievementDTO> achievements;
}
