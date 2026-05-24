package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CoinLeaderDTO {
    private Long userId;
    private String username;
    private Long coins;
    private Integer rank;
}

