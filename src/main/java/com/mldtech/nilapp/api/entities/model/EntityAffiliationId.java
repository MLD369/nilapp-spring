package com.mldtech.nilapp.api.entities.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityAffiliationId implements Serializable {

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "affiliation_id")
    private Long affiliationId;
}

