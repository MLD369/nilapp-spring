package com.mldtech.nilapp.api.users.children.UserAchievement.model;

//import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.achievements.model.Achievement;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "user_achievements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //TODO make singluar
    @Column(name = "user_achievements_id")
    private Long UserAchievement;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "earned_at")
    private LocalDate earnedAt;
}




