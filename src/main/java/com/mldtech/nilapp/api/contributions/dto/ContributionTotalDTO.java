package com.mldtech.nilapp.api.contributions.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContributionTotalDTO {

    private Long totalSteps;
    private Long totalNilCoins;
}
