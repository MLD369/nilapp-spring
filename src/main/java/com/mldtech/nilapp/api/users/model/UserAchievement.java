package com.mldtech.nilapp.api.users.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "achievements_id")
    private Long achievementsId;
}


