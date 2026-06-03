package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupDetailsDTO {

    private Long groupId;
    private String groupName;

    private List<SubGroupDTO> subGroups;          // empty for now
    private List<EntityDetailsDTO> affiliatedEntities;   // from group_entities
    private List<GoalProgressDTO> activeGoals;    // from goal_instance
}

