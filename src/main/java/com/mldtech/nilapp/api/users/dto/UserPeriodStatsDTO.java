package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPeriodStatsDTO {
    private String period;          // "2026-05-24" or "2026-W21" or "2026-05" or "2026"
    private Long totalSteps;
    private Long totalCoins;
}

