package com.mldtech.nilapp.api.group.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "varchar")
    private String entities;
}
