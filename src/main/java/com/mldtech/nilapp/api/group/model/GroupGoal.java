package com.mldtech.nilapp.api.group.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_goal_id")
    private Long groupGoalId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    @Column(name = "allocation_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPct;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active")
    private Boolean isActive;
}

