package com.mldtech.nilapp.api.users.children.UserProgress.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_progress_id")
    private Long userProgressId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_goal_id", nullable = false)
    private UserGoal userGoal;

    @Column(name = "steps_contributed", nullable = false)
    private Integer stepsContributed;

    @Column(name = "coins_contributed", nullable = false)
    private Integer coinsContributed;

    @Column(name = "recorded_at", insertable = false, updatable = false)
    private LocalDateTime recordedAt;
}

