package com.mldtech.nilapp.api.contributions.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ContributionRequestDTO {
    private Long userId;
    private Long entityId;
    private Long campaignId;
    private int stepsContributed;
    private int coinsContributed;
    private BigDecimal conversionRate;
    private String adSessionId;
    private Long payoutBatchId;
    private Integer contributionStatusId;
    private Integer contributionTypeId;

    // This captures the exact incoming combined JSON allocations structure directly
    private String allocationSnapshot;
}
