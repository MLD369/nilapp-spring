package com.mldtech.nilapp.api.friend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mldtech.nilapp.api.friend.children.FriendStatus.model.FriendStatus;
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
    @JsonBackReference
    private User user;


    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    @JsonBackReference
    private User friend;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private FriendStatus friendStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonProperty("friendId")
    public Long getFriendId() {
        return friend != null ? getFriend().getUserId() : null;
    }

    @JsonProperty("friendUsername")
    public String getFriendUsername() {
        return friend != null ? getFriend().getUsername() : null;
    }

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? getUser().getUserId() : null;
    }

    @JsonProperty("userUsername")
    public String getUserUsername() {
        return user != null ? getUser().getUsername() : null;
    }
}

