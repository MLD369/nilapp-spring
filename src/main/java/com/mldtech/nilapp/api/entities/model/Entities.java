package com.mldtech.nilapp.api.entities.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.entities.children.EntityAffiliation.model.EntityAffiliation;
import com.mldtech.nilapp.api.entities.children.EntityType.model.EntityType;
import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.payout_batches.model.PayoutBatch;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "type",nullable = false)
    private EntityType entityType;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entities", orphanRemoval = true)
    private List<EntityAffiliation> entityAffiliations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityId", orphanRemoval = true)
    private List<Contribution> contributions;

//    @OneToMany
//    @ManyToOne
//    @JoinColumn(name = "user_id",nullable = false)
//    private User user;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entity", orphanRemoval = true)
    @JsonManagedReference
    private List<UserEntity> userEntities;


//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entities", orphanRemoval = true)
//    private List<PayoutBatch> payoutBatches;


//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entities", orphanRemoval = true)
//    private List<EntityGoals> entityGoals = new ArrayList<>();
}

