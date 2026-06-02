package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EntityDetailsDTO {

    private Long entityId;
    private String name;
    private String abbreviation;
    private String associatedSchool;

    private EntityTypeDTO entityType;
    private List<EntityAffiliationDTO> affiliations;
    private List<GoalProgressDTO> activeGoals;

    // OPTIONAL: include raw entity if you want
    // private Entities entity;
}
