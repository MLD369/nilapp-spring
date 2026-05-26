package com.mldtech.nilapp.api.users.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EntityGoalProgressDTO {
    private Long entityId;
    private String entityName;
    private List<GoalProgressDTO> goals;
}
