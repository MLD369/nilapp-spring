package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DetailedLeaderboardResponse {

    // Date range
    private String startDate;
    private String endDate;

    // Leaderboard category: steps, coins, streaks, entityNil
    private String leaderboardCategory;

    // Filter metadata
    private String filterType;     // user, entity, group, city, state, gender, etc.
    private String filterString;   // Atlanta, GA, Red Cross, Team A, etc.

    // Top-N selection: 25, 50, 100, etc.
    private Integer limit;

    // Filtered + ranked results
    private List<?> results;
}
