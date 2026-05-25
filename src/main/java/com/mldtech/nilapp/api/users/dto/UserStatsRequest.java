package com.mldtech.nilapp.api.users.dto;

import lombok.Data;

@Data
public class UserStatsRequest {
    private String startDate;   // "2026-05-01"
    private String endDate;     // "2026-05-24"
    private String period;      // DAILY | WEEKLY | MONTHLY | YEARLY
}
