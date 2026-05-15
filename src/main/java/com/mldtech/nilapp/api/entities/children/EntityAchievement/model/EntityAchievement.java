package com.mldtech.nilapp.api.entities.children.EntityAchievement.model;

import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.entities.model.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "entity_achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_achievement_id")
    private Long entityAchievementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    private Entities entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "earned_at")
    private LocalDate earnedAt;
}
