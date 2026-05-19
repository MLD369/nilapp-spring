package com.mldtech.nilapp.api.users.dto;

import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ContributionDTO {
    private Long contributionId;
    private Long amount;
    private int stepsContributed;
    private int coinsContributed;
    private String statusCode;
    private String statusLabel;
    private String statusDescription;


    private Long entityId;

    private Long campaignId;

    private BigDecimal conversionRate; // TODO might not want to show

    private LocalDateTime createdAt;

}
