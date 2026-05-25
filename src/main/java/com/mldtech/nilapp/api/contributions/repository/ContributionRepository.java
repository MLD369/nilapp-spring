package com.mldtech.nilapp.api.contributions.repository;

import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.dto.EntityNilLeaderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByUserId(Long userId);

    List<Contribution> findByEntityId(Long entityId);

    List<Contribution> findByCampaignId(Long campaignId);

//    List<Contribution> findByStatusId(Integer statusId);
    List<Contribution> findByUserIdAndEntityId(Long userId, Long entityId);

    // TODO update the view to get the  entity or group name
    @Query(value =
            "SELECT month_label, year_label, entity_id, total_steps, total_coins " +
                    " FROM user_entity_stats_view " +
                    " WHERE user_id = :userId " +
                    "  AND (:entityId IS NULL OR entity_id = :entityId) " +
                    "  AND contribution_status_id = :statusId " +
                    "  AND created_at BETWEEN :startDate AND :endDate " +
                    " ORDER BY month_label, year_label",
            nativeQuery = true)
    List<Object[]> getEntityStats(
            @Param("userId") Long userId,
            @Param("entityId") Long entityId,
            @Param("statusId") Long statusId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    // TODO update the view to get the  entity or group name
    @Query(value =
            "SELECT month_label, year_label, group_id, total_steps, total_coins " +
                    " FROM user_group_stats_view " +
                    " WHERE user_id = :userId " +
                    "  AND (:groupId IS NULL OR group_id = :groupId) " +
                    "  AND contribution_status_id = :statusId " +
                    "  AND created_at BETWEEN :startDate AND :endDate " +
                    " ORDER BY month_label, year_label",
            nativeQuery = true)
    List<Object[]> getGroupStats(
            @Param("userId") Long userId,
            @Param("groupId") Long groupId,
            @Param("statusId") Long statusId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT new com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO(
        COALESCE(SUM(c.stepsContributed), 0),
        COALESCE(SUM(c.coinsContributed), 0)
    )
    FROM Contribution c
    WHERE c.userId = :userId
      AND c.createdAt BETWEEN :start AND :end
""")
    ContributionTotalDTO getUserMonthlyTotals(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
    SELECT u.userId, u.username, SUM(c.coinsContributed)
    FROM Contribution c
    JOIN User u ON u.userId = c.userId
    WHERE c.createdAt BETWEEN :start AND :end
    GROUP BY u.userId, u.username
    ORDER BY SUM(c.coinsContributed) DESC
""")
    List<Object[]> getTopCoins(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new com.mldtech.nilapp.api.users.dto.EntityNilLeaderDTO(
        e.entityId,
        e.name,
        SUM(c.coinsContributed)
    )
    FROM Contribution c
    JOIN Entities e ON e.entityId = c.entityId
    WHERE c.createdAt BETWEEN :start AND :end
    GROUP BY e.entityId, e.name
    ORDER BY SUM(c.coinsContributed) DESC
""")
    List<EntityNilLeaderDTO> getEntityNilLeaderboard(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT c FROM Contribution c
    WHERE c.userId = :userId
      AND c.createdAt >= :start
      AND c.createdAt < :end
""")
    List<Contribution> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

}

