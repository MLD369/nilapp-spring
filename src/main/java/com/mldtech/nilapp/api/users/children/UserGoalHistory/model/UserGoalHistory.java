package com.mldtech.nilapp.api.users.children.UserGoalHistory.model;

//import com.mldtech.nilapp.api.goals.model.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_goal_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_goal_history_id")
    private Long UserGoalHistory;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    @Column(name = "coins_contributed")
    private int coinsContributed;
    @Column(name = "steps_contributed")
    private int stepsContributed;
    @Column(name = "allocation_pct")
    private BigDecimal allocationPercentage;
}

