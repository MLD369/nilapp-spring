package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityAllocationDTO {
    private Long entityId;
    private Integer percentage;
}

