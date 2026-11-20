package com.mldtech.nilapp.api.users.repository;

//import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.dto.UserSearchDTO;
import com.mldtech.nilapp.api.users.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);



    // ======================================================================
    // 1. DIRECT USER TABLE FILTERS (User, City, County, State, Country, Gender)
    // ======================================================================

    @Query(value = """
        SELECT u.user_id FROM users u
        WHERE CAST(u.user_id AS TEXT) = :filter
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :filter, '%'))
    """, nativeQuery = true)
    Set<Long> findUserIdsByUsernameOrId(@Param("filter") String filter);

    @Query(value = "SELECT u.user_id FROM users u WHERE LOWER(u.city) = LOWER(:city)", nativeQuery = true)
    Set<Long> findUserIdsByCityIgnoreCase(@Param("city") String city);

    @Query(value = "SELECT u.user_id FROM users u WHERE LOWER(u.county) = LOWER(:county)", nativeQuery = true)
    Set<Long> findUserIdsByCountyIgnoreCase(@Param("county") String county);

    @Query(value = "SELECT u.user_id FROM users u WHERE LOWER(u.state) = LOWER(:state)", nativeQuery = true)
    Set<Long> findUserIdsByStateIgnoreCase(@Param("state") String state);

    @Query(value = "SELECT u.user_id FROM users u WHERE LOWER(u.country) = LOWER(:country)", nativeQuery = true)
    Set<Long> findUserIdsByCountryIgnoreCase(@Param("country") String country);

    @Query(value = "SELECT u.user_id FROM users u WHERE LOWER(u.gender) = LOWER(:gender)", nativeQuery = true)
    Set<Long> findUserIdsByGenderIgnoreCase(@Param("gender") String gender);

    // ======================================================================
    // 2. AGE GROUP FILTERS (Uses `birthday` with fallback to `birth_year`)
    // ======================================================================

    // ======================================================================
    // 2. AGE GROUP FILTERS (Safely casts VARCHAR `birth_year` to INTEGER)
    // ======================================================================

    @Query(value = """
        SELECT u.user_id FROM users u
        WHERE (u.birthday IS NOT NULL AND u.birthday <= :maxDob)
           OR (u.birthday IS NULL 
               AND u.birth_year IS NOT NULL 
               AND u.birth_year ~ '^\\d{4}$'
               AND CAST(u.birth_year AS INTEGER) <= EXTRACT(YEAR FROM CAST(:maxDob AS DATE)))
    """, nativeQuery = true)
    Set<Long> findUserIdsBornOnOrBefore(@Param("maxDob") LocalDate maxDob);

    @Query(value = """
        SELECT u.user_id FROM users u
        WHERE (u.birthday IS NOT NULL AND u.birthday BETWEEN :minDob AND :maxDob)
           OR (u.birthday IS NULL 
               AND u.birth_year IS NOT NULL 
               AND u.birth_year ~ '^\\d{4}$'
               AND CAST(u.birth_year AS INTEGER) BETWEEN EXTRACT(YEAR FROM CAST(:minDob AS DATE)) AND EXTRACT(YEAR FROM CAST(:maxDob AS DATE)))
    """, nativeQuery = true)
    Set<Long> findUserIdsBornBetween(@Param("minDob") LocalDate minDob, @Param("maxDob") LocalDate maxDob);


    // ======================================================================
    // 3. ENTITY & GROUP MEMBERSHIP FILTERS (Matches by ID or Name)
    // ======================================================================

    @Query(value = """
        SELECT DISTINCT ue.user_id
        FROM user_entities ue
        JOIN entities e ON ue.entity_id = e.entity_id
        WHERE ue.left_at IS NULL
          AND (CAST(e.entity_id AS TEXT) = :entityFilter OR LOWER(e.name) = LOWER(:entityFilter))
    """, nativeQuery = true)
    Set<Long> findActiveUserIdsByEntity(@Param("entityFilter") String entityFilter);

    @Query(value = """
        SELECT DISTINCT ug.user_id
        FROM user_groups ug
        JOIN groups g ON ug.group_id = g.group_id
        WHERE ug.left_at IS NULL
          AND (CAST(g.group_id AS TEXT) = :groupFilter OR LOWER(g.name) = LOWER(:groupFilter))
    """, nativeQuery = true)
    Set<Long> findActiveUserIdsByGroup(@Param("groupFilter") String groupFilter);

    // ======================================================================
    // 4. AFFILIATION FILTERS (Sports Conference & Region via Entity Affiliations)
    // ======================================================================

    @Query(value = """
        SELECT DISTINCT ue.user_id
        FROM user_entities ue
        JOIN entity_affiliations ea ON ue.entity_id = ea.entity_id
        JOIN affiliations a ON ea.affiliation_id = a.affiliation_id
        JOIN affiliation_types at ON a.affiliation_type_id = at.affiliation_type_id
        WHERE ue.left_at IS NULL
          AND UPPER(at.name) LIKE UPPER(CONCAT('%', :typeKeyword, '%'))
          AND (CAST(a.affiliation_id AS TEXT) = :affiliationFilter OR LOWER(a.name) = LOWER(:affiliationFilter))
    """, nativeQuery = true)
    Set<Long> findUserIdsByAffiliationTypeAndName(
            @Param("typeKeyword") String typeKeyword,
            @Param("affiliationFilter") String affiliationFilter
    );

    // ======================================================================
    // 5. FRIENDS FILTER (Includes current user + accepted friends)
    // ======================================================================

    @Query(value = """
        SELECT :currentUserId
        UNION
        SELECT CASE
            WHEN f.user_id = :currentUserId THEN f.friend_id
            ELSE f.user_id
        END
        FROM friendships f
        WHERE (f.user_id = :currentUserId OR f.friend_id = :currentUserId)
          AND UPPER(f.status) = 'ACCEPTED'
    """, nativeQuery = true)
    Set<Long> findFriendUserIds(@Param("currentUserId") Long currentUserId);

    @Query("""
        SELECT new com.mldtech.nilapp.api.users.dto.UserSearchDTO(u.userId, u.username)
        FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT(:query, '%'))
        ORDER BY u.username ASC
    """)
    List<UserSearchDTO> searchByUsernamePrefix(@Param("query") String query, Pageable pageable);

}

