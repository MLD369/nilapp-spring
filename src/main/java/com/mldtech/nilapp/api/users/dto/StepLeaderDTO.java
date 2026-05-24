package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StepLeaderDTO {
    private Long userId;
    private String username;
    private Long totalSteps;
    private Integer rank;
}

