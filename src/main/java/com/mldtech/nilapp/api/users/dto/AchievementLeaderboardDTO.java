package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementLeaderboardDTO {
    private Long userId;
    private String username;
    private Long totalAchievements;
    private Integer rank;
}

