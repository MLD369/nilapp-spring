package com.mldtech.nilapp.mapper;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.dto.EntityAllocationDTO;
import com.mldtech.nilapp.api.users.dto.GroupAllocationDTO;

import java.util.List;
import java.util.Map;

public class ContributionMapper {

    @SuppressWarnings("unchecked")
    public static ContributionDTO toDTO(Contribution c) {

        Map<String, Object> snapshot = c.getAllocationSnapshot();

        List<EntityAllocationDTO> entityAllocations = null;
        List<GroupAllocationDTO> groupAllocations = null;

        if (snapshot != null) {

            // ENTITY ALLOCATIONS
            List<Map<String, Object>> entityList =
                    (List<Map<String, Object>>) snapshot.get("entityAllocations");

            if (entityList != null) {
                entityAllocations = entityList.stream()
                        .map(e -> EntityAllocationDTO.builder()
                                .entityId(Long.valueOf(e.get("entityId").toString()))
                                .percentage(Integer.valueOf(e.get("percentage").toString()))
                                .build()
                        ).toList();
            }

            // GROUP ALLOCATIONS
            List<Map<String, Object>> groupList =
                    (List<Map<String, Object>>) snapshot.get("groupAllocations");

            if (groupList != null) {
                groupAllocations = groupList.stream()
                        .map(g -> GroupAllocationDTO.builder()
                                .groupId(Long.valueOf(g.get("groupId").toString()))
                                .percentage(Integer.valueOf(g.get("percentage").toString()))
                                .build()
                        ).toList();
            }
        }

        return ContributionDTO.builder()
                .contributionId(c.getContributionId())
                .stepsContributed(c.getStepsContributed())
                .coinsContributed(c.getCoinsContributed().intValue())
                .statusCode(c.getContributionStatus().getCode())
                .statusLabel(c.getContributionStatus().getLabel())
                .statusDescription(c.getContributionStatus().getDescription())
                .campaignId(c.getCampaignId())
                .conversionRate(c.getConversionRate())
                .createdAt(c.getCreatedAt())
                .entityAllocations(entityAllocations)
                .groupAllocations(groupAllocations)
                .build();
    }
}

