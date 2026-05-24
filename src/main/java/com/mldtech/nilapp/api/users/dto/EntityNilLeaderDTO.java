package com.mldtech.nilapp.api.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityNilLeaderDTO {
    private Long entityId;
    private String entityName;
    private Long totalCoins;
    private Integer rank;
    public EntityNilLeaderDTO(Long entityId, String entityName, Long totalCoins) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.totalCoins = totalCoins;
    }
}
