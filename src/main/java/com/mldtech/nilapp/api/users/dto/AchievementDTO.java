package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AchievementDTO {
    private Long achievementId;
    private String achievement;
    private String description;
    private String badge;
}
