package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContributionDTO {
    private Long contributionId;
    private Long amount;
    private String type;
}
