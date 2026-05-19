package com.mldtech.nilapp.api.groupstat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupStat {

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
    private Long coins;

    @Column(name = "steps")
    private Long steps;

    @Column(name = "allocation_percentage")
    private Integer allocationPercentage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

