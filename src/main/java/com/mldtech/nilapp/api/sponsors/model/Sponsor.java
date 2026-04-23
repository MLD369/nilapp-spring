package com.mldtech.nilapp.api.sponsors.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sponsors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sponsor_id")
    private Long sponsorId;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_active")
    private Boolean isActive;
}
