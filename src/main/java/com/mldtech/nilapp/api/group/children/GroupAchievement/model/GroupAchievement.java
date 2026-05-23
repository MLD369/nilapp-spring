package com.mldtech.nilapp.api.group.children.GroupAchievement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.group.model.Group;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_achievements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "achievement_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_achievements_id")
    private Long groupAchievementsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    @JsonBackReference
    private Achievement achievement;


    @Column(name = "earned_at")
    private LocalDateTime earnedAt;
}

