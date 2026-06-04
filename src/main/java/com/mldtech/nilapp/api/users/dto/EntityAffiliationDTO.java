package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityAffiliationDTO {
    private Long id;
    private String affiliationName;
    private String affiliationType;
    private Long affiliationId;
    private String affiliationTypeDescription;
    private String affiliationTypeName;

}

