package com.mldtech.nilapp.api.global.service;

import com.mldtech.nilapp.api.UserAchievement.repository.UserAchievementRepository;
import com.mldtech.nilapp.api.UserStreak.controller.UserStreakController;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
import com.mldtech.nilapp.api.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.UserGroup.repository.UserGroupRepository;
import com.mldtech.nilapp.api.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.api.users.model.User;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private static final Logger log = LoggerFactory.getLogger(LeaderboardService.class);
    private final ContributionRepository contributionRepository;
    private final DailyStatRepository dailyStatRepository;
    private final EntityRepository entityRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserEntityRepository userEntityRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserRepository userRepository;

    public List<StreakLeaderDTO> getTopStreaks() {
        List<StreakLeaderDTO> list = userStreakRepository.getTopStreaks()
                .stream()
                .map(r -> StreakLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .currentStreak(((Number) r[2]).intValue())
                        .build())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<StepLeaderDTO> getTopSteps(LocalDate start, LocalDate end) {
        List<StepLeaderDTO> list = dailyStatRepository.getTopSteps(start, end)
                .stream()
                .map(r -> StepLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .totalSteps(((Number) r[2]).longValue())
                        .build())
                .toList();
        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<CoinLeaderDTO> getTopCoins(LocalDateTime start, LocalDateTime end) {
        List<CoinLeaderDTO> list =  contributionRepository.getTopCoins(start, end)
                .stream()
                .map(r -> CoinLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .coins(((Number) r[2]).longValue())
                        .build())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<EntityNilLeaderDTO> getEntityNilLeaders(LocalDateTime start, LocalDateTime end) {
        List<EntityNilLeaderDTO> list = contributionRepository.getEntityNilLeaderboard(start, end);

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));

        return list;
    }



    public GlobalLeaderboardResponse getLeaderboard(String startDate, String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startTs = start.atStartOfDay();
        LocalDateTime endTs = end.atTime(23, 59, 59);

        return GlobalLeaderboardResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .stepLeaders(getTopSteps(start, end))
                .coinLeaders(getTopCoins(startTs, endTs))
                .streakLeaders(getTopStreaks())
                .entityNilLeaders(getEntityNilLeaders(startTs, endTs))
                .build();
    }
    public DetailedLeaderboardResponse getLeaderboardFilteredRanks(
            Long currentUserId,
            String startDate,
            String endDate,
            String leaderboardCategory,
            String filterType,
            String filterString,
            Integer limit
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startTs = start.atStartOfDay();
        LocalDateTime endTs = end.atTime(23, 59, 59);

        // 1. Load raw leaderboard based on category
        List<?> rawList = switch (leaderboardCategory.toLowerCase()) {
            case "steps" -> getTopSteps(start, end);
            case "coins" -> getTopCoins(startTs, endTs);
            case "streaks" -> getTopStreaks();
            case "achievements" -> getTopAchievements(startTs, endTs); // TODO not working
            case "entitynil" -> getEntityNilLeaders(startTs, endTs);
            default -> throw new IllegalArgumentException("Invalid leaderboard category: " + leaderboardCategory);
        };

        // 2. Apply filters (skip if "all" or blank)
        List<?> filtered;
        if (filterString == null || filterString.isBlank() || "all".equalsIgnoreCase(filterString)) {
            filtered = rawList;
        } else {
            filtered = applyLeaderboardFilter(rawList, currentUserId,leaderboardCategory, filterType, filterString);
        }

        // 3. Re-rank filtered list
        AtomicInteger rank = new AtomicInteger(1);
        filtered.forEach(item -> {
            try {
                item.getClass().getMethod("setRank", Integer.class)
                        .invoke(item, rank.getAndIncrement());
            } catch (Exception ignored) {}
        });

        // 4. Apply top-N limit
        int safeLimit = (limit != null && limit > 0) ? limit : 50;
        List<?> limited = filtered.stream()
                .limit(safeLimit)
                .toList();

        // 5. Build response
        return DetailedLeaderboardResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .leaderboardCategory(leaderboardCategory)
                .filterType(filterType)
                .filterString(filterString)
                .limit(safeLimit)
                .results(limited)
                .build();
    }


    // ---------------------------------------------------
    // Filter Engine
    // ---------------------------------------------------

    private List<?> applyLeaderboardFilter(
            List<?> rawList,
            Long currentUserId,
            String leaderboardCategory,
            String filterType,
            String filterString
    ) {
        if (filterType == null || filterString == null || "all".equalsIgnoreCase(filterString)) {
            return rawList;
        }

        // Special handling if category is "entitynil" (since items are Entities, not Users)
        if ("entitynil".equalsIgnoreCase(leaderboardCategory)) {
            Set<Long> allowedEntityIds = resolveMatchingEntityIds(currentUserId, filterType, filterString);
            if (allowedEntityIds == null) return rawList;
            return rawList.stream()
                    .filter(item -> allowedEntityIds.contains(extractEntityId(item)))
                    .toList();
        }

        // Standard User Leaderboards (steps, coins, streaks, achievements)
        Set<Long> allowedUserIds = resolveMatchingUserIds(currentUserId, filterType, filterString);
        if (allowedUserIds == null) {
            return rawList;
        }

        return rawList.stream()
                .filter(item -> allowedUserIds.contains(extractUserId(item)))
                .toList();
    }
    private Set<Long> resolveMatchingUserIds(Long currentUserId, String filterType, String filterString) {
        String normalized = filterString.trim();

        return switch (filterType.toLowerCase()) {
            // 1. User table fields
            case "user"       -> userRepository.findUserIdsByUsernameOrId(normalized);
            case "city"       -> userRepository.findUserIdsByCityIgnoreCase(normalized);
            case "county"     -> userRepository.findUserIdsByCountyIgnoreCase(normalized);
            case "state"      -> userRepository.findUserIdsByStateIgnoreCase(normalized);
            case "country"    -> userRepository.findUserIdsByCountryIgnoreCase(normalized);
            case "gender"     -> userRepository.findUserIdsByGenderIgnoreCase(normalized);

            // 2. Dynamic Age Group Ranges (e.g., "13-17", "18-24", "25-34", "35-44", "45-54", "55+")
            case "age", "agegroup", "age_group" -> resolveUserIdsByAgeRange(normalized); // TODO only 25plus works so fix or make ui use plus

            // 3. Memberships & Social
            case "entity"     -> userRepository.findActiveUserIdsByEntity(normalized);
            case "group"      -> userRepository.findActiveUserIdsByGroup(normalized);
            case "friends"    -> userRepository.findFriendUserIds(currentUserId); // includes currentUserId + accepted friends

            // 4. Entity Affiliations (Sports Conference & Region)
            case "conference", "sports_conference" -> userRepository.findUserIdsByAffiliationTypeAndName("CONFERENCE", normalized);
            // todo there is not region but use affilication types
//            case "region"                          -> userRepository.findUserIdsByAffiliationTypeAndName("REGION", normalized);

            default -> null; // Unknown filterType -> no filtering
        };
    }
    private Set<Long> resolveUserIdsByAgeRange(String ageFilter) {
        LocalDate today = LocalDate.now();
        // Preserve raw string before trim to catch unencoded '+' which HTTP decodes as a trailing space
        boolean isOpenEndedPlus = ageFilter.endsWith("+")
                || ageFilter.endsWith(" ")
                || ageFilter.toLowerCase().endsWith("plus");

        String cleaned = ageFilter.replace("+", "")
                .replaceAll("(?i)plus", "")
                .trim();

        // 1. Open-ended range: "25+", "25%2B", "25 ", or "25plus" // TODO only 25plus works so fix or make ui use plus
        if (isOpenEndedPlus) {
            int minAge = Integer.parseInt(cleaned);
            LocalDate maxDob = today.minusYears(minAge);
            return userRepository.findUserIdsBornOnOrBefore(maxDob);
        }

        // 2. Bounded range: "18-24" or "25-34"
        String[] parts = ageFilter.split("-");
        if (parts.length == 2) {
            int minAge = Integer.parseInt(parts[0].trim());
            int maxAge = Integer.parseInt(parts[1].trim());
            LocalDate minDob = today.minusYears(maxAge + 1).plusDays(1);
            LocalDate maxDob = today.minusYears(minAge);
            return userRepository.findUserIdsBornBetween(minDob, maxDob);
        }

        // 3. Exact single age: "25"
        int exactAge = Integer.parseInt(ageFilter.trim());
        LocalDate minDob = today.minusYears(exactAge + 1).plusDays(1);
        LocalDate maxDob = today.minusYears(exactAge);
        return userRepository.findUserIdsBornBetween(minDob, maxDob);
    }


    private List<AchievementLeaderboardDTO> getTopAchievements(LocalDateTime startTs, LocalDateTime endTs) {
        List<Object[]> rows = userAchievementRepository.findTopUserAchievementRows(startTs, endTs);

        List<AchievementLeaderboardDTO> results = new ArrayList<>();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            String username = (String) row[1];
            Long count = ((Number) row[2]).longValue();

            results.add(AchievementLeaderboardDTO.builder()
                    .userId(userId)
                    .username(username)
                    .totalAchievements(count)
                    .build());
        }
        return results;
    }


    private Set<Long> resolveMatchingEntityIds(Long currentUserId, String filterType, String filterString) {
        if (filterType == null || filterString == null || "all".equalsIgnoreCase(filterString)) {
            return null;
        }

        String normalized = filterString.trim();

        return switch (filterType.toLowerCase()) {
            case "entity"                          -> entityRepository.findEntityIdsByNameOrId(normalized);
            case "entity_type", "type"             -> entityRepository.findEntityIdsByEntityType(normalized);
            case "group"                           -> entityRepository.findEntityIdsByGroup(normalized);
            case "conference", "sports_conference" -> entityRepository.findEntityIdsByAffiliationTypeAndName("CONFERENCE", normalized);
            case "region"                          -> entityRepository.findEntityIdsByAffiliationTypeAndName("REGION", normalized);
            case "my_entities", "user"             -> entityRepository.findActiveEntityIdsForUser(currentUserId);
            default -> null;
        };
    }
    private Long extractEntityId(Object item) {
        if (item == null) {
            return null;
        }
        try {
            Object val = item.getClass().getMethod("getEntityId").invoke(item);
            return val != null ? Long.valueOf(val.toString()) : null;
        } catch (Exception e) {
            log.warn("Could not extract entityId from leaderboard item of type {}", item.getClass().getSimpleName());
            return null;
        }
    }


    // Extract userId from any leaderboard DTO
    private Long extractUserId(Object dto) {
        try {
            return (Long) dto.getClass().getMethod("getUserId").invoke(dto);
        } catch (Exception e) {
            throw new RuntimeException("DTO missing getUserId()");
        }
    }

    // -----------------------------
    // User Filters
    // -----------------------------

    private boolean filterUser(Long userId, String filterString) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        return user.getUsername().equalsIgnoreCase(filterString);
    }

    private boolean filterUserCity(Long userId, String city) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && city.equalsIgnoreCase(user.getCity());
    }

    private boolean filterUserState(Long userId, String state) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && state.equalsIgnoreCase(user.getState());
    }

    private boolean filterUserGender(Long userId, String gender) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && gender.equalsIgnoreCase(user.getGender());
    }
    private boolean filterUserCountry(Long userId, String gender) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && gender.equalsIgnoreCase(user.getCountry());
    }

    // -----------------------------
    // Entity Filters
    // -----------------------------
    //            entity type
    //            associated school
    //            sporting affiliations
    //            affilations type
    //            conference

    private boolean filterEntity(Long userId, String filterString) {
        List<UserEntity> entities = userEntityRepository.findByUser_UserId(userId);

        return entities.stream().anyMatch(ue ->
                ue.getEntity().getName().equalsIgnoreCase(filterString)
                        || ue.getEntity().getAssociatedSchool().equalsIgnoreCase(filterString)
        );
    }

    // -----------------------------
    // Group Filters
    // -----------------------------
//    group
//    sub group
    //            conference
    private boolean filterGroup(Long userId, String filterString) {
        List<UserGroup> groups = userGroupRepository.findByUser_UserId(userId);

        return groups.stream().anyMatch(ug ->
                ug.getGroup().getName().equalsIgnoreCase(filterString)
        );
    }
}

//    public void getLeaderboardFilteredRanks(String startDate, String endDate,String leaderboardCategory,String filterType,String filterString){
//        // by city,state,gender,age group (separate by ranges?), entity,group, sports conference,region,device(ios,android1)
////        leaderboardCategory steps,streaks,coin,achievements
//
//        /*from userrepository (entity is users)
//                age
//                state
//                country
//                city
//                county
//                langauage
//                timezone?
//                gender
//        from entity repository or entity child  (entity is entities)
//                by entity
//                entity type
//                associated school
//                sporting affiliations
//                affilations type
//
//
//        from group repository (entity is groups)
//            group
//            sub group
//
//                */
//    }


