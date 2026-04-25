package com.mldtech.nilapp.api.users.model;

//import com.mldtech.nilapp.api.entities.model.Entities;
//import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
//import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
//import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
//import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 50,name = "first_name")
    private String firstName;

    @Column(nullable = false, length = 50,name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, columnDefinition = "text")
    private String password;

    @Column(name = "total_lifetime_coins")
    private Long totalLifetimeCoins;

    private LocalDate birthday;
    @Column(name = "birth_year")
    private String birthYear;

    @Column(insertable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false,name = "updated_at")
    private LocalDateTime updatedAt;

    private String state;
    private String country;
    private String city;
    private String county;

    private String language;
    private String timezone;
    @Column(nullable = false, length = 50,name = "force_password_change")
    private Boolean forcePasswordChange;
    @Column(nullable = false, length = 50,name = "session_id")
    private String sessionId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserEntity> userEntities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserGroup> userGroups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserAchievement> userAchievements;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserGoal> userGoals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserGoalHistory> userGoalHistories;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Friend> userFriends;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<DailyStat> dailyStats;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Contribution> contributions;

/// Used as sample for each
//    @Column(name ="middle_name",nullable = false,columnDefinition = "varchar")
//    private String middleName;
//    @ManyToOne
//    @JoinColumn(name = "user_status_id",nullable = false)
//    private UserStatus userStatus;
//@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
//private List<Friend> userFriends;
}

