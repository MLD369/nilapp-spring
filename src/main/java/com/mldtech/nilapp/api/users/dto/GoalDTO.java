package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoalDTO {
    private Long goalId;
    private String goalName;
    private Boolean isActive;
}
