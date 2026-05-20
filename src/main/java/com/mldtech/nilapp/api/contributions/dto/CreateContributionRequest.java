package com.mldtech.nilapp.api.contributions.dto;

import com.mldtech.nilapp.api.users.dto.EntityAllocationDTO;
import com.mldtech.nilapp.api.users.dto.GroupAllocationDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateContributionRequest {
    private Long userId;
    private Long entityId; // optional if you still use it
    private int coins;
    private int steps;

    private List<EntityAllocationDTO> entityAllocations;
    private List<GroupAllocationDTO> groupAllocations;

    private BigDecimal adValue;
    private BigDecimal coinValue;
}


