package com.mldtech.nilapp.api.contributions.children.ContributionStatus.model;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.entities.model.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "contribution_statuses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"contributions"})
public class ContributionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_status_id")
    private Integer contributionStatusId;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(length = 100)
    private String label;

    @Column(columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "contributionStatus")
    private List<Contribution> contributions;
}

