package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsSummaryDTO {
    private Double avgDeviceSteps;
    private Double avgClaimedSteps;
    private Double avgCoins;
    private Double avgClaimRatio;
    private List<UserPeriodStatsDTO> periodStats;
}

