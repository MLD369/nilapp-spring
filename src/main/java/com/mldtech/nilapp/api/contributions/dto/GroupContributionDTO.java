package com.mldtech.nilapp.api.contributions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupContributionDTO {
    private Long groupId;
    private String groupName;
    private Long totalSteps;
    private Long totalNilCoins;
}

