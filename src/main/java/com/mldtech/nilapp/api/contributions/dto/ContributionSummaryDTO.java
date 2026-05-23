package com.mldtech.nilapp.api.contributions.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContributionSummaryDTO {

    private Long userId;
    private Long totalNilCoins;   // sum of all coins contributed
    private Long totalSteps;
    private List<GroupContributionDTO> groupContributions;
    private List<EntityContributionDTO> entityContributions;
}
