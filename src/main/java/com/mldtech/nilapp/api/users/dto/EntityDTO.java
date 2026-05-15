package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityDTO {
    private Long entityId;
    private String name;
    private String abbreviation;
}
