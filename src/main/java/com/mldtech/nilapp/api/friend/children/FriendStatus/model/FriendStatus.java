package com.mldtech.nilapp.api.friend.children.FriendStatus.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friend_statuses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_status_id")
    private Integer friendStatusId;

    @Column(length = 9)
    private String status;

    @Column(length = 50)
    private String description;
}

