package com.mldtech.nilapp.api.affiliationType.model;

import com.mldtech.nilapp.api.affiliations.model.Affiliation;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "affiliation_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"affiliation"})
public class AffiliationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_type_id")
    private Long affiliationTypeId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "affiliationType")
    private List<Affiliation> affiliation;
}

