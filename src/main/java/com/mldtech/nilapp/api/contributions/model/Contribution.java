package com.mldtech.nilapp.api.contributions.model;

import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionType.model.ContributionType;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @JoinColumn(name = "contribution_status_id",nullable = false)
    private ContributionStatus contributionStatus;

    @Type(JsonType.class)
    @Column(name = "allocation_snapshot",columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> allocationSnapshot;


    //    {
//        "entityAllocations": { "5": 60, "7": 40 },
//        "groupAllocations": { "3": 70, "9": 30 },
//        "adValue": 0.004,
//            "coinValue": 1,
//            "coinsEarned": 100,
//            "steps": 8000
//    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_type_id", nullable = false)
    private ContributionType contributionType;
//    @Column(name = "contribution_status_id", nullable = false)
//    private Long statusId;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
//    private List<Goal> entityGoals;
}
