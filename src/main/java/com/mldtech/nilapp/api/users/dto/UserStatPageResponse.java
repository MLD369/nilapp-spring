package com.mldtech.nilapp.api.users.dto;

import com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatPageResponse {
    private UserStatsResponse stats;
    private UserGoalDashboardResponse dashboard;
    private UserProfileResponse completedGoals;
    private UserProfileResponse achievements;
    private ContributionTotalDTO contributionTotal;
}
