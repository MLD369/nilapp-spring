package com.mldtech.nilapp.api.contributions.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContributionSummaryDTO {

    private Long userId;
    private Long totalNilCoins;   // Sum of all coins contributed
    private Long totalDeviceSteps; // Raw fitness tracking steps from mobile sensor
    private Long totalClaimedSteps; // Total steps backed by validated ad views
    private List<GroupContributionDTO> groupContributions;
    private List<EntityContributionDTO> entityContributions;
}
