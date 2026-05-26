package com.mldtech.nilapp.api.goals.model;

import com.mldtech.nilapp.api.achievements.model.Achievement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"achievement"})
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "goal_types_id")
    private int goalTypesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(length = 255)
    private String goal;

    @Column(length = 255)
    private String description;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "goal_lock_date")
    private LocalDateTime goalLockDate;

}

