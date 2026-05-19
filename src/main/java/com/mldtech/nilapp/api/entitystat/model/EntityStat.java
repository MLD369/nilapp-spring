package com.mldtech.nilapp.api.entitystat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "entity_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityStat {

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
    private Long coins;

    @Column(name = "steps")
    private Long steps;

    @Column(name = "allocation_percentage")
    private Integer allocationPercentage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

