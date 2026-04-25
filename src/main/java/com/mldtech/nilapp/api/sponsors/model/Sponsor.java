package com.mldtech.nilapp.api.sponsors.model;

import com.mldtech.nilapp.api.sponsors.children.SponserCampaign.model.SponsorCampaign;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sponsorId", orphanRemoval = true)
    private List<SponsorCampaign> sponsorCampaigns;
}
