package com.mldtech.nilapp.api.sponsors.children.SponserCampaign.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sponsor_campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "sponsor_id")
    private Long sponsorId;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "payout_per_step", precision = 10, scale = 6)
    private BigDecimal payoutPerStep;

    @Column(name = "payout_per_coin", precision = 10, scale = 6)
    private BigDecimal payoutPerCoin;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
