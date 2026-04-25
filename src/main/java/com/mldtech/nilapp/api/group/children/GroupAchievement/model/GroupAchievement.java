package com.mldtech.nilapp.api.group.children.GroupAchievement.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "group_achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAchievement {



    @Id
    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "earned_at")
    private LocalDate earnedAt;
}
