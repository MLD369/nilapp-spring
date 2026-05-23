package com.mldtech.nilapp.api.users.children.UserGoal.model;

//import com.mldtech.nilapp.api.goals.model.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoal {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_goal_id")
    private Long userGoalId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne
//    @JoinColumn(name = "goal_id")
//    private Goal goal;
//    @Column(name = "goal_instance_id", precision = 5, scale = 2, nullable = false)
//    private Long goalInstanceId;
    @ManyToOne
    @JoinColumn(name = "goal_instance_id")
    private GoalInstance goalInstance;

    @Column(name = "allocation_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPct;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_complete")
    private Boolean isComplete;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}
