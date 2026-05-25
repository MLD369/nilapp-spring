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
    private Double avgSteps;
    private Double avgCoins;
    private List<UserPeriodStatsDTO> periodStats;
}

