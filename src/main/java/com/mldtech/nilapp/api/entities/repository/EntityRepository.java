package com.mldtech.nilapp.api.entities.repository;

import com.mldtech.nilapp.api.entities.model.Entities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface EntityRepository extends JpaRepository<Entities, Long> {

    @Query(value = """
        SELECT e.entity_id FROM entities e
        WHERE e.is_active = true
          AND (CAST(e.entity_id AS TEXT) = :filter
               OR LOWER(e.name) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(e.abbreviation) = LOWER(:filter)
               OR LOWER(e.associated_school) LIKE LOWER(CONCAT('%', :filter, '%')))
    """, nativeQuery = true)
    Set<Long> findEntityIdsByNameOrId(@Param("filter") String filter);

    @Query(value = """
        SELECT DISTINCT e.entity_id
        FROM entities e
        JOIN entity_types et ON e.type = et.entity_types_id
        WHERE e.is_active = true
          AND (CAST(et.entity_types_id AS TEXT) = :typeFilter OR LOWER(et.type) = LOWER(:typeFilter))
    """, nativeQuery = true)
    Set<Long> findEntityIdsByEntityType(@Param("typeFilter") String typeFilter);

    @Query(value = """
        SELECT DISTINCT ge.entity_id
        FROM group_entities ge
        JOIN groups g ON ge.group_id = g.group_id
        WHERE CAST(g.group_id AS TEXT) = :groupFilter
           OR LOWER(g.name) = LOWER(:groupFilter)
    """, nativeQuery = true)
    Set<Long> findEntityIdsByGroup(@Param("groupFilter") String groupFilter);

    @Query(value = """
        SELECT DISTINCT ea.entity_id
        FROM entity_affiliations ea
        JOIN affiliations a ON ea.affiliation_id = a.affiliation_id
        JOIN affiliation_types at ON a.affiliation_type_id = at.affiliation_type_id
        WHERE UPPER(at.name) LIKE UPPER(CONCAT('%', :typeKeyword, '%'))
          AND (CAST(a.affiliation_id AS TEXT) = :affiliationFilter OR LOWER(a.name) = LOWER(:affiliationFilter))
    """, nativeQuery = true)
    Set<Long> findEntityIdsByAffiliationTypeAndName(
            @Param("typeKeyword") String typeKeyword,
            @Param("affiliationFilter") String affiliationFilter
    );

    @Query(value = """
        SELECT DISTINCT ue.entity_id
        FROM user_entities ue
        WHERE ue.user_id = :userId
          AND ue.left_at IS NULL
    """, nativeQuery = true)
    Set<Long> findActiveEntityIdsForUser(@Param("userId") Long userId);


}
