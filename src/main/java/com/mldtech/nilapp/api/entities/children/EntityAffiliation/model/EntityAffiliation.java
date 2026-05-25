package com.mldtech.nilapp.api.entities.children.EntityAffiliation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.affiliations.model.Affiliation;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "entity_affiliations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"entities"})
public class EntityAffiliation {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_affiliations_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    private Entities entities;


    @ManyToOne
    @JoinColumn(name = "affiliation_id", nullable = false)
    private Affiliation affiliation;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
