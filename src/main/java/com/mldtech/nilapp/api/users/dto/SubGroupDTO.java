package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubGroupDTO {
    private Long groupId;
    private String groupName;
}
