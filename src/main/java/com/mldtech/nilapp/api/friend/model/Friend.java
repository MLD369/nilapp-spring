package com.mldtech.nilapp.api.friend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "friends",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friends_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    private Integer status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

