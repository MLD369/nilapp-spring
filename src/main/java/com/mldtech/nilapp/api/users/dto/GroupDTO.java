package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDTO {
    private Long groupId;
    private String name;
}
