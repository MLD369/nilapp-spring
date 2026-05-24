package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GlobalLeaderboardResponse {
    private String startDate;
    private String endDate;
    private List<EntityNilLeaderDTO> entityNilLeaders;
    private List<StreakLeaderDTO> streakLeaders;
    private List<CoinLeaderDTO> coinLeaders;
    private List<StepLeaderDTO> stepLeaders;
}

