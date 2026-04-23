package com.mldtech.nilapp.api.users.model;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "user_entities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @EmbeddedId
    private UserEntityId id;
}


