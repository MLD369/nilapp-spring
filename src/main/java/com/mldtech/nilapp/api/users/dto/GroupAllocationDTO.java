package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupAllocationDTO {
    private Long groupId;
    private Integer percentage;
}
