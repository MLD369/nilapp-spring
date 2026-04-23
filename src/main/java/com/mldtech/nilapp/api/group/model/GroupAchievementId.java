package com.mldtech.nilapp.api.group.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupAchievementId implements Serializable {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "achievements_id")
    private Long achievementsId;
}
