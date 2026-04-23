package com.mldtech.nilapp.api.users.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_goal_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_goal_history_id")
    private Long userGoalHistoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

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
