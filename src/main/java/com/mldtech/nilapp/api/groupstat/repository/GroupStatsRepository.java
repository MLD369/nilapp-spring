package com.mldtech.nilapp.api.groupstat.repository;

import com.mldtech.nilapp.api.groupstat.model.GroupStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing group-level allocation statistics.
 * Validates financial contributions for custom friend-circles and challenge clusters.
 */
@Repository
public interface GroupStatsRepository extends JpaRepository<GroupStatsEntity, Long> {

    /**
     * Aggregates the total coins contributed by a specific user to a designated group.
     * Ensures users meet the financial gate required to unlock collective achievements.
     *
     * @param userId  The target user tracking identifier
     * @param groupId The user-group or tournament cluster tracking identifier
     * @return Total lifetime coins safely cast to a long type boundary
     */
    @Query("""
        SELECT COALESCE(SUM(gs.coins), 0) 
        FROM GroupStatsEntity gs 
        WHERE gs.userId = :userId 
          AND gs.groupId = :groupId
        """)
    long sumCoinsByUserAndGroup(
            @Param("userId") Long userId,
            @Param("groupId") Long groupId
    );
}

