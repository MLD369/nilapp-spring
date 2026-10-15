package com.mldtech.nilapp.api.entitystat.repository;



import com.mldtech.nilapp.api.entitystat.model.EntityStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing entity-level allocation statistics.
 * Uses high-performance indexed queries to prevent aggregate calculation bottlenecks.
 */
@Repository
public interface EntityStatsRepository extends JpaRepository<EntityStatsEntity, Long> {

    /**
     * Aggregates the total coins contributed by a specific user to a designated entity.
     * Failsafe default returns 0 if no match or no active historical entries exist.
     *
     * @param userId   The target user tracking identifier
     * @param entityId The targeted fraternity, school, or collective organization identifier
     * @return Total lifetime coins safely cast to a long type boundary
     */
    @Query("""
        SELECT COALESCE(SUM(es.coins), 0) 
        FROM EntityStatsEntity es 
        WHERE es.userId = :userId 
          AND es.entityId = :entityId
        """)
    long sumCoinsByUserAndEntity(
            @Param("userId") Long userId,
            @Param("entityId") Long entityId
    );
}

