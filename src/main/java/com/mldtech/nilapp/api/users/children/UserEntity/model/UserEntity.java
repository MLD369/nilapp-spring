package com.mldtech.nilapp.api.users.children.UserEntity.model;


//import com.mldtech.nilapp.api.entities.model.Entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_entities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "entity_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_entities_id")
    private Long userEntitiesId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "entity_id")
    @JsonBackReference
    private Entities entity;

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }

    @JsonProperty("entityId")
    public Long getEntityId() {
        return entity != null ? entity.getEntityId() : null;
    }

    @JsonProperty("name")
    public String getName() {
        return entity != null ? entity.getName() : null;
    }

    @JsonProperty("associatedSchool")
    public String getAssociatedSchool() {
        return entity != null ? entity.getAssociatedSchool() : null;
    }

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;
}



