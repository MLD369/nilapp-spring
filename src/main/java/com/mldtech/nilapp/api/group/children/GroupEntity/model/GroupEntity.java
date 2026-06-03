package com.mldtech.nilapp.api.group.children.GroupEntity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.group.model.Group;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@ToString(exclude = {"user"})
@Table(name = "group_entities", uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "entity_id"})})
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_entity_id")
    private Long groupEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private Entities entities;
}

