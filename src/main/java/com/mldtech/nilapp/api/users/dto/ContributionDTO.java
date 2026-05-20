package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContributionDTO {

    private Long contributionId;
    private int amount;
    private int stepsContributed;
    private int coinsContributed;

    private String statusCode;
    private String statusLabel;
    private String statusDescription;

    private Long campaignId;

    private BigDecimal conversionRate; // TODO might not want to show

    private LocalDateTime createdAt;

    // NEW: JSONB allocations mapped to DTO lists
    private List<EntityAllocationDTO> entityAllocations;
    private List<GroupAllocationDTO> groupAllocations;

}
