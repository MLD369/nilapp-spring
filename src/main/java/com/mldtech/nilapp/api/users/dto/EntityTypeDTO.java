package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityTypeDTO {
    private Long entityTypesId;
    private String type;
    private String description;
}

