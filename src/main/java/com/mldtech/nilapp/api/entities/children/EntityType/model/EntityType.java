package com.mldtech.nilapp.api.entities.children.EntityType.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entity_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_types_id")
    private Long entityTypesId;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(length = 100)
    private String description;
}

