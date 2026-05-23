package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementCountDTO {
    private Long achievementId;
    private String achievementName;
    private Long timesEarned;
    private LocalDateTime lastEarnedAt;
    private String entityName;
    private String groupName;
    private LocalDateTime earnedAt;
}

