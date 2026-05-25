package com.mldtech.nilapp.api.entities.children.EntityType.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mldtech.nilapp.api.entities.model.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "entity_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"entities"})
public class EntityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_types_id")
    private Long entityTypesId;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(length = 100)
    private String description;

    @OneToMany(mappedBy = "entityType")
    @JsonManagedReference
    private List<Entities> entities;

}

