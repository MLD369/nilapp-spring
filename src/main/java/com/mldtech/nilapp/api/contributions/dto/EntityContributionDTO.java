package com.mldtech.nilapp.api.contributions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityContributionDTO {
    private Long entityId;
    private String entityName;
    private Long totalSteps;
    private Long totalNilCoins;
}

