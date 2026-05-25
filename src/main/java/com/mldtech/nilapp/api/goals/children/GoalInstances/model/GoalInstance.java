package com.mldtech.nilapp.api.goals.children.GoalInstances.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.group.model.Group;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goal_instances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"goal","group","entity"})
public class GoalInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_instance_id")
    private Long goalInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    private Entities entity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "current_progress", precision = 10, scale = 6)
    private BigDecimal currentProgress;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_complete")
    private Boolean isComplete;
}

