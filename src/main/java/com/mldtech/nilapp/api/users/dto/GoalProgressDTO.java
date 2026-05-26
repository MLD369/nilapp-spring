package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GoalProgressDTO {

    private Long goalId;
    private String goalName;

    private Long requiredSteps;
    private Long requiredCoins;

    private Long currentAmount;
    private double progressPct;
    private boolean isComplete;

    private Long stepsContributed;
    private Long coinsContributed;
    private Long stepsNeeded;
    private Long coinsNeeded;
    private BigDecimal dollarAmount;
}

