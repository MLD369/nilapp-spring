package com.mldtech.nilapp.api.achievements.model;

import com.mldtech.nilapp.api.achievements.children.AchievementType.model.AchievementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Long achievementId;

    @ManyToOne
    @JoinColumn(name = "achievement_type",nullable = false)
    private AchievementType achievementType;

    @Column(length = 255)
    private String achievement;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String badge;

    @Column(name = "required_steps")
    private Long requiredSteps;

    @Column(name = "required_distance")
    private Long requiredDistance;

    @Column(name = "required_coins")
    private Long requiredCoins;
}

