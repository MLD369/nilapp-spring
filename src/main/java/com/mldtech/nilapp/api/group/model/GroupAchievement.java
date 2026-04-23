package com.mldtech.nilapp.api.group.model;


import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupAchievement implements Serializable {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "achievements_id")
    private Long achievementsId;
}



