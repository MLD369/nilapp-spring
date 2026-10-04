package com.mldtech.nilapp.api.global.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
import com.mldtech.nilapp.api.users.children.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.api.users.model.User;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final ContributionRepository contributionRepository;
    private final DailyStatRepository dailyStatRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserEntityRepository userEntityRepository;
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
//            case "achievements" -> getTopStreaks();  // TODO fix bug
//            case "entitynil" -> getEntityNilLeaders(startTs, endTs);  // TODO fix bug
            default -> throw new RuntimeException("Invalid leaderboard category");
        };

        // 2. Apply filters
        // 2. If filterString = "all", skip filtering entirely
        List<?> filtered;
        if ("all".equalsIgnoreCase(filterString)) {
            filtered = rawList;
        } else {
            filtered = applyLeaderboardFilter(rawList, filterType, filterString);
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
        List<?> limited = filtered.stream()
                .limit(limit)
                .toList();

        // 5. Build response
        return DetailedLeaderboardResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .leaderboardCategory(leaderboardCategory)
                .filterType(filterType)
                .filterString(filterString)
                .limit(limit)
                .results(limited)
                .build();
    }

    // ---------------------------------------------------
    // Filter Engine
    // ---------------------------------------------------

    private List<?> applyLeaderboardFilter(List<?> rawList, String filterType, String filterString) {

        return rawList.stream()
                .filter(item -> {

                    Long userId = extractUserId(item);

                    return switch (filterType.toLowerCase()) {

                        case "user" -> filterUser(userId, filterString);
                        case "entity" -> filterEntity(userId, filterString);
                        case "group" -> filterGroup(userId, filterString);


                        // Expandable: city, state, gender, device, region, conference
                        case "city" -> filterUserCity(userId, filterString);
                        case "state" -> filterUserState(userId, filterString);
                        case "gender" -> filterUserGender(userId, filterString);
                        case "country" -> filterUserCountry(userId, filterString);
//                        case "region" -> filterRegion(userId, filterString);  // TODO fix bug

                        default -> true;
                    };

                })
                .toList();
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


