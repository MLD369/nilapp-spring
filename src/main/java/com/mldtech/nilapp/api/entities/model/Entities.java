package com.mldtech.nilapp.api.entities.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_id")
    private Long entityId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String abbreviation;

    @Column(name = "associated_school", length = 100)
    private String associatedSchool;

    @Column
    private Long type;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "secondary_color")
    private String secondaryColor;

    @Column(name = "sporting_affiliations")
    private Long sportingAffiliations;

    @Column(name = "tax_id", length = 20)
    private String taxId;
}

