package com.mldtech.nilapp.api.achievements.children.AchievementType.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievement_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_type_id")
    private int achievementTypeId;

    @Column(length = 50)
    private String type;

    @Column(length = 100)
    private String meaning;
}

