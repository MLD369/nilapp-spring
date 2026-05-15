package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyStatDTO {
    private Long dailyStatId;
    private Long steps;
    private Long distance;
    private String date;
}
