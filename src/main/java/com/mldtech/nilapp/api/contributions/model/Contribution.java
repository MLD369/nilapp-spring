package com.mldtech.nilapp.api.contributions.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_id")
    private Long contributionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "steps_contributed")
    private Integer stepsContributed;

    @Column(name = "coins_contributed")
    private Integer coinsContributed;

    @Column(name = "conversion_rate", precision = 10, scale = 6)
    private BigDecimal conversionRate;

    @Column(name = "ad_session_id", length = 100)
    private String adSessionId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "payout_batch_id")
    private Long payoutBatchId;

    @Column(name = "status", nullable = false)
    private Long statusId;
}
