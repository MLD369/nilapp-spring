package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StreakLeaderDTO {
    private Long userId;
    private String username;
    private int currentStreak;
    private Integer rank;
}

