package com.mldtech.nilapp.api.goals.children.GoalTypes.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "goal_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_types_id")
    private Integer goalTypesId;

    @Column(name = "type", length = 255, nullable = false)
    private String type; // APP, ENTITY, GROUP

    @Column(name = "description", length = 255)
    private String description;
}

