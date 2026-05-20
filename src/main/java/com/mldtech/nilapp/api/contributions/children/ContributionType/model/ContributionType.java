package com.mldtech.nilapp.api.contributions.children.ContributionType.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contribution_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_type_id")
    private int contributionTypeId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;   // AD, SURVEY, BONUS, etc.

    @Column(nullable = false, length = 100)
    private String label;

    @Column(length = 255)
    private String description;
}
