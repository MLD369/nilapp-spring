package com.mldtech.nilapp.api.users.children.UserEntity.model;


//import com.mldtech.nilapp.api.entities.model.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "entity_id")
    private Entities entity;
}



