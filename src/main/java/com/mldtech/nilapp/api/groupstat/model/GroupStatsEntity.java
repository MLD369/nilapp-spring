package com.mldtech.nilapp.api.groupstat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_stats", schema = "public")
public class GroupStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_stat_id")
    private Long groupStatId;

    @Column(name = "contribution_id", nullable = false)
    private Long contributionId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

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

    // --- Constructor Definitions ---
    public GroupStatsEntity() {}

    public GroupStatsEntity(Long contributionId, Long groupId, Long userId, Long coins, Long steps, Integer allocationPercentage) {
        this.contributionId = contributionId;
        this.groupId = groupId;
        this.userId = userId;
        this.coins = coins;
        this.steps = steps;
        this.allocationPercentage = allocationPercentage;
    }

    // --- Getters and Setters ---
    public Long getGroupStatId() { return groupStatId; }
    public void setGroupStatId(Long groupStatId) { this.groupStatId = groupStatId; }

    public Long getContributionId() { return contributionId; }
    public void setContributionId(Long contributionId) { this.contributionId = contributionId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

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
}

