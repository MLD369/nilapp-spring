package com.mldtech.nilapp.api.users.children.UserStreak.model;

import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_streak_id")
    private Long userStreakId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_streak", nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    @Builder.Default
    private Integer longestStreak = 0;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @Column(name = "streak_shields", nullable = false)
    @Builder.Default
    private Integer streakShields = 0;

    @Column(name = "streak_savers", nullable = false)
    @Builder.Default
    private Integer streakSavers = 0;

    // --- NEW LIFETIME AUDITING COLUMNS ---
    @Column(name = "total_shields_earned", nullable = false)
    @Builder.Default
    private Integer totalShieldsEarned = 0;

    @Column(name = "total_shields_used", nullable = false)
    @Builder.Default
    private Integer totalShieldsUsed = 0;

    @Column(name = "total_video_savers_used", nullable = false)
    @Builder.Default
    private Integer totalVideoSaversUsed = 0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
