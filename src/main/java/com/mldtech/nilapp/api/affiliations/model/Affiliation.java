package com.mldtech.nilapp.api.affiliations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.affiliationType.model.AffiliationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "affiliations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Affiliation {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_id")
    private Long affiliationId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "affiliation_type_id", nullable = false)
    private AffiliationType affiliationType;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}

