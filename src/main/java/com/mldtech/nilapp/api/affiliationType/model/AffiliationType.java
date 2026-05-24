package com.mldtech.nilapp.api.affiliationType.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "affiliation_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_type_id")
    private Long affiliationTypeId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}

