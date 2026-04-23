package com.mldtech.nilapp.api.contributions.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contribution_statuses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_status_id")
    private Long contributionStatusId;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(length = 100)
    private String label;

    @Column(columnDefinition = "text")
    private String description;
}

