package com.mldtech.nilapp.api.affiliations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "affiliations_id")
    private Long affiliationsId;

    @Column(nullable = false)
    private String name;
}

