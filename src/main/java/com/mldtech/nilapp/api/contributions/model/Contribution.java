package com.mldtech.nilapp.api.contributions.model;

import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private Long userId;  // TODO DTO

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

    @ManyToOne
    @JoinColumn(name = "status",nullable = false)
    private ContributionStatus contributionStatus;


//    @Column(name = "contribution_status_id", nullable = false)
//    private Long statusId;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
//    private List<Goal> entityGoals;
}
