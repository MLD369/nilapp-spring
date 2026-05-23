package com.mldtech.nilapp.mapper;

import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.users.dto.EntityDTO;


public class EntityMapper {

    public static EntityDTO toDTO(Entities e) {
        return EntityDTO.builder()
                .entityId(e.getEntityId())
                .name(e.getName())
                .abbreviation(e.getAbbreviation())
                .associatedSchool(e.getAssociatedSchool())
                .entityType(e.getEntityType() != null ? e.getEntityType().getType() : null)
                .build();
    }
}

