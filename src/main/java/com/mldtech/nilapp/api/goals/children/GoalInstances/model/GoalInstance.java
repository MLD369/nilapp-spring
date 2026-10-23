package com.mldtech.nilapp.api.goals.children.GoalInstances.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.group.model.Group;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private BigDecimal currentProgress; // TODO remove?

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_complete")
    private Boolean isComplete;

    @Column(name = "steps_contributed", nullable = false)
    private Long stepsContributed = 0L;

    @Column(name = "coins_contributed", nullable = false)
    private Long coinsContributed = 0L;

    @Column(name = "steps_needed")
    private Long stepsNeeded;

    @Column(name = "coins_needed")
    private Long coinsNeeded;

    @Column(name = "dollar_amount", nullable = false)
    private BigDecimal dollarAmount = BigDecimal.ZERO;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Entity lifecycle hook executed right before a standard row merge update command drops into PostgreSQL.
     * Ensures our app-layer modifications keep timestamps accurate without manual configuration parameters.
     */
    @PreUpdate
    protected void onRowUpdateLifecycleAction() {
        this.updatedAt = LocalDateTime.now();
    }
}


