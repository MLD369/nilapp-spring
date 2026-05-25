package com.mldtech.nilapp.api.group.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
//import com.mldtech.nilapp.api.group.children.GroupGoalHistory.model.GroupGoalHistory;
import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userGroups","groupAchievements","entities"})
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(length = 100)
    private String name;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    @JsonManagedReference
    private List<GroupAchievement> groupAchievements;

    @ManyToMany
    @JoinTable(
            name = "group_entities",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id")
    )
    @JsonManagedReference
    private List<Entities> entities;


//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
//    private List<GroupGoalHistory> groupGoalHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private List<UserGroup> userGroups = new ArrayList<>();
// USer groups
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
//    private List<UserGoalHistory> userGoalHistories;
}
