package com.mldtech.nilapp.api.entities.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "entity_affiliations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityAffiliation {

    @EmbeddedId
    private EntityAffiliationId id;
}
