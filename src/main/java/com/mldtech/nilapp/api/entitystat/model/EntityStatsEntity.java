package com.mldtech.nilapp.api.entitystat.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// TODO use lombook
@Entity
@Table(name = "entity_stats", schema = "public")
public class EntityStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_stat_id")
    private Long entityStatId;

    @Column(name = "contribution_id", nullable = false)
    private Long contributionId;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coins")
    private Long coins = 0L;

    @Column(name = "steps")
    private Long steps = 0L;

    @Column(name = "allocation_percentage", nullable = false)
    private Integer allocationPercentage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revenue_generated", precision = 15, scale = 6)
    private BigDecimal revenueGenerated = BigDecimal.ZERO;

    @Column(name = "company_profit_retained", precision = 15, scale = 6)
    private BigDecimal companyProfitRetained = BigDecimal.ZERO;

    // --- Constructor Definitions ---
    public EntityStatsEntity() {}

    public EntityStatsEntity(Long entityStatId, Long contributionId, Long entityId, Long userId, Long coins, Long steps, Integer allocationPercentage, LocalDateTime createdAt, BigDecimal revenueGenerated, BigDecimal companyProfitRetained) {
        this.entityStatId = entityStatId;
        this.contributionId = contributionId;
        this.entityId = entityId;
        this.userId = userId;
        this.coins = coins;
        this.steps = steps;
        this.allocationPercentage = allocationPercentage;
        this.createdAt = createdAt;
        this.revenueGenerated = revenueGenerated;
        this.companyProfitRetained = companyProfitRetained;
    }

    public EntityStatsEntity(Long contributionId, Long entityId, Long userId, Long coins, Long steps, Integer allocationPercentage) {
        this.contributionId = contributionId;
        this.entityId = entityId;
        this.userId = userId;
        this.coins = coins;
        this.steps = steps;
        this.allocationPercentage = allocationPercentage;
    }

    // --- Getters and Setters ---
    public Long getEntityStatId() { return entityStatId; }
    public void setEntityStatId(Long entityStatId) { this.entityStatId = entityStatId; }

    public Long getContributionId() { return contributionId; }
    public void setContributionId(Long contributionId) { this.contributionId = contributionId; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCoins() { return coins; }
    public void setCoins(Long coins) { this.coins = coins; }

    public Long getSteps() { return steps; }
    public void setSteps(Long steps) { this.steps = steps; }

    public Integer getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getRevenueGenerated() {
        return revenueGenerated;
    }

    public void setRevenueGenerated(BigDecimal revenueGenerated) {
        this.revenueGenerated = revenueGenerated;
    }

    public BigDecimal getCompanyProfitRetained() {
        return companyProfitRetained;
    }

    public void setCompanyProfitRetained(BigDecimal companyProfitRetained) {
        this.companyProfitRetained = companyProfitRetained;
    }
}

