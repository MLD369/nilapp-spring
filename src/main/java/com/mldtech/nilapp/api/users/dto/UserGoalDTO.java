package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
public class UserGoalDTO {
    private Long userGoalId;
    private Long userId;
    private Long goalInstanceId;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private BigDecimal allocationPct;
    private boolean isActive;
    private boolean isComplete;
}

