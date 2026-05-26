package com.mldtech.nilapp.api.users.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserGoalDashboardResponse {
    private Long userId;
    private String username;

    private List<GoalProgressDTO> userGoals;
    private List<EntityGoalProgressDTO> entityGoals;
    private List<GroupGoalProgressDTO> groupGoals;
}

