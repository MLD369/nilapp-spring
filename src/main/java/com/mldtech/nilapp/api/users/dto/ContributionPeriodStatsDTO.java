package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContributionPeriodStatsDTO {

    private String periodLabel; // "2026-04" or "2026"
    private Long entityId;
    private Long groupId;

    private int totalSteps;
    private int totalCoins;

    private double avgSteps;
    private double avgCoins;
}

