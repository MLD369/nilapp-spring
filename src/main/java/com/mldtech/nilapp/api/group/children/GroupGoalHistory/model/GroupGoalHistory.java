package com.mldtech.nilapp.api.group.children.GroupGoalHistory.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_goal_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupGoalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_goal_history_id")
    private Long groupGoalHistoryId;

    @Column(name = "group_id", nullable = false)
    private Long group;

    @Column(name = "group_goal_id", nullable = false)
    private Long groupGoalId;

    @Column(name = "allocation_pct", precision = 5, scale = 2)
    private BigDecimal allocationPct;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "coins_contributed")
    private Integer coinsContributed;

    @Column(name = "steps_contributed")
    private Integer stepsContributed;
}

