package com.mldtech.nilapp.api.users.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "entity_id")
    private Long entityId;
}

