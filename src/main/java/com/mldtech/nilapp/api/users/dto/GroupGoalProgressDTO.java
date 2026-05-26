package com.mldtech.nilapp.api.users.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupGoalProgressDTO {
    private Long groupId;
    private String groupName;
    private List<GoalProgressDTO> goals;
}

