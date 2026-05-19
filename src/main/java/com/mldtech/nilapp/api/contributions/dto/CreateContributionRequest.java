package com.mldtech.nilapp.api.contributions.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CreateContributionRequest {
    private Long userId;
    private Long entityId;
    private int coins;
    private int steps;
    private Map<Long, Integer> entityAllocations;
    private Map<Long, Integer> groupAllocations;
    private BigDecimal adValue;
    private BigDecimal coinValue;
}

