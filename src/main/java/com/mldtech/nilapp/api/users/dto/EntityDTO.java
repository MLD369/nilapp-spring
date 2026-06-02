package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EntityDTO {
    private Long entityId;
    private String name;
    private String abbreviation;
    private String associatedSchool;
    private String entityTypeName;
    private String entityType;
    private boolean joined;
    private String joinedAt;
    private String leftAt;
    private List<EntityAffiliationDTO> affiliations;
    private List<GoalProgressDTO> activeGoals;
}