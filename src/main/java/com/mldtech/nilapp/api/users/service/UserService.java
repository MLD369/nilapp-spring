package com.mldtech.nilapp.api.users.service;


//import com.mldtech.nilapp.api.friend.model.Friend;
//import com.mldtech.nilapp.api.friend.repository.FriendRepository;
//import com.mldtech.nilapp.api.users.children.UserAchievement.repository.UserAchievementRepository;
//import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
//import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.repository.UserGoalHistoryRepository;
//import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
//import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.dto.EntityContributionDTO;
import com.mldtech.nilapp.api.contributions.dto.GroupContributionDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.friend.repository.FriendRepository;
import com.mldtech.nilapp.api.users.children.UserAchievement.repository.UserAchievementRepository;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import com.mldtech.nilapp.helper.PeriodKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContributionRepository contributionRepository;

//    private final UserEntityRepository userEntityRepository;
//    private final UserGroupRepository userGroupRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserGoalRepository userGoalRepository;
//    private final UserGoalHistoryRepository userGoalHistoryRepository;
    private final FriendRepository friendRepository;
//    @Transactional   /// TODO change this to the right way with dtos
//    public UserProfileResponse getUserProfile(Long userId) {
//
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
////        var entities = userEntityRepository.findByUserEntitiesByUserId(userId)
////                .stream()
////                .map(ue -> ue.getEntity().getEntityId())
////                .toList();
////
////        var groups = userGroupRepository.findByUserGroupUserId(userId)
////                .stream()
////                .map(ug -> ug.getGroup().getGroupId())
////                .toList();
////
////        var achievements = userAchievementRepository.findByUserUserId(userId)
////                .stream()
////                .map(ua -> ua.getAchievement().getAchievementId())
////                .toList();
////
////        var activeGoals = userGoalRepository.findByUserUserIdAndIsActiveTrue(userId);
////
////        var goalHistory = userGoalHistoryRepository.findByUserUserId(userId);
////
////        var friends = friendRepository.findByFriendUserId(userId)
////                .username(user.getUsername())
////                .email(user.getEmail())
////                .entityIds(entities)
////                .stream()
////                .map(Friend::getId)
////                .toList();
//
//        return UserProfileResponse.builder()
//                .userId(userId)
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .totalLifetimeCoins(user.getTotalLifetimeCoins())
//                .groups(user.getUserGroups().stream().toList())
//                .achievements(user.getUserAchievements().stream().toList())
//                .goals(user.getUserGoals().stream().toList())
////                .goalHistory(user.getUserGoalHistories().stream().toList())
//                .entities(user.getUserEntities().stream().toList())
//                .contributions(user.getContributions().stream().toList())
//                .dailyStats(user.getDailyStats().stream().toList())
//                .friends(user.getUserFriends().stream().toList())
//                .build();
//    }
public CustomResponse<UserProfileResponse> getUserProfile(Long userId) {

    var user = userRepository.findById(userId)
            .orElse(null);

    if (user == null) {
        return new CustomResponse<>(
                "User not found",
                HttpStatus.BAD_REQUEST,
                "400"
        );
    }

    UserProfileResponse profile = UserProfileResponse.builder()
            .userId(userId)
            .username(user.getUsername())
            .email(user.getEmail())
            .totalLifetimeCoins(user.getTotalLifetimeCoins())

            .groups(
                    user.getUserGroups().stream()
                            .map(ug -> GroupDTO.builder()
                                    .groupId(ug.getGroup().getGroupId())
                                    .name(ug.getGroup().getName())
                                    .build()
                            )
                            .toList()
            )

            .achievements(
                    user.getUserAchievements().stream()
                            .map(ua -> AchievementDTO.builder()
                                    .achievementId(ua.getAchievement().getAchievementId())
                                    .achievementName(ua.getAchievement().getAchievement())
                                    .description(ua.getAchievement().getDescription())
                                    .badge(ua.getAchievement().getBadge())
                                    .build()
                            )
                            .toList()
            )

            .goals(
                    user.getUserGoals().stream()
                            .map(g -> GoalDTO.builder()
                                    .goalId(g.getUserGoalId())
//                                    .goalName(g.getGoal().getGoal())
                                    .isActive(g.getIsActive())
                                    .build()
                            )
                            .toList()
            )

            .entities(
                    user.getUserEntities().stream()
                            .map(ue -> EntityDTO.builder()
                                    .entityId(ue.getEntity().getEntityId())
                                    .name(ue.getEntity().getName())
                                    .abbreviation(ue.getEntity().getAbbreviation())
                                    .build()
                            )
                            .toList()
            )

            .friends(
                    user.getUserFriends().stream()
                            .map(f -> FriendDTO.builder()
                                    .friendId(f.getFriend().getUserId())
                                    .friendUsername(f.getFriend().getUsername())
                                    .statusId(f.getFriendStatus().getFriendStatusId())
                                    .status(f.getFriendStatus().getStatus())
                                    .statusDescription(f.getFriendStatus().getDescription())
                                    .build()
                            )
                            .toList()
            )

            .build();


    return new CustomResponse<>(
            profile,
            HttpStatus.OK,
            "200"
    );
}

    public UserProfileResponse getUserAchievements(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var achievements = user.getUserAchievements().stream()
                .map(ua -> AchievementDTO.builder()
                        .achievementId(ua.getAchievement().getAchievementId())
                        .achievementName(ua.getAchievement().getAchievement())
                        .description(ua.getAchievement().getDescription())
                        .badge(ua.getAchievement().getBadge())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .achievements(achievements)
                .build();
    }
    public UserProfileResponse getUserGoals(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var goals = user.getUserGoals().stream()
                .map(g -> GoalDTO.builder()
                        .goalId(g.getUserGoalId())
//                        .goalName(g.getGoal().getGoal())
                        .isActive(g.getIsActive())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .goals(goals)
                .build();
    }
    public CustomResponse<UserProfileResponse> getUserCompletedGoals(Long userId, String startDate, String endDate) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDateTime start = (startDate != null)
                    ? LocalDate.parse(startDate).atStartOfDay()
                    : LocalDate.now().withDayOfMonth(1).atStartOfDay();

            LocalDateTime end = (endDate != null)
                    ? LocalDate.parse(endDate).atTime(23, 59, 59)
                    : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);

            var completedGoals = userGoalRepository.findByUserUserIdAndIsCompleteTrueAndCompletedAtBetween(userId, start, end)
                    .stream()
                    .map(g -> GoalDTO.builder()
                            .goalId(g.getUserGoalId())
                            .goalName(g.getGoalInstance().getGoal().getGoal())
                            .entityName(g.getGoalInstance().getEntity() != null ? g.getGoalInstance().getEntity().getName() : null)
                            .groupName(g.getGoalInstance().getGroup() != null ? g.getGoalInstance().getGroup().getName() : null)
                            .isActive(false)
                            .build())
                    .toList();

            if (completedGoals.isEmpty()) {
                return new CustomResponse<>(
                        "No completed goals found for the specified date range.",
                        null,
                        HttpStatus.NO_CONTENT,
                        "204"
                );
            }

            var response = UserProfileResponse.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .goals(completedGoals)
                    .build();

            return new CustomResponse<>(
                    "Completed goals fetched successfully.",
                    response,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error fetching completed goals: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }

    public UserProfileResponse getUserIncompletedGoals(Long userId, String startDate, String endDate) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var incompletedGoals = user.getUserGoals().stream()
                .filter(g -> Boolean.FALSE.equals(g.getIsComplete()))
                .map(g -> GoalDTO.builder()
                        .goalId(g.getUserGoalId())
//                        .goalName(g.getGoal().getGoal())
                        .isActive(g.getIsActive())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .goals(incompletedGoals)
                .build();
    }
    public UserProfileResponse getUserGroups(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var groups = user.getUserGroups().stream()
                .map(ug -> GroupDTO.builder()
                        .groupId(ug.getGroup().getGroupId())
                        .name(ug.getGroup().getName())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .groups(groups)
                .build();
    }
    public CustomResponse<UserProfileResponse> getUserEntities(Long userId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var entities = user.getUserEntities().stream()
                    .map(ue -> EntityDTO.builder()
                            .entityId(ue.getEntity().getEntityId())
                            .name(ue.getEntity().getName())
                            .abbreviation(ue.getEntity().getAbbreviation())
                            .build())
                    .toList();

            var response = UserProfileResponse.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .entities(entities)
                    .build();

            if (entities.isEmpty()) {
                return new CustomResponse<>(
                        "User found but has no associated entities.",
                        response,
                        HttpStatus.NO_CONTENT,
                        String.valueOf(HttpStatus.NO_CONTENT.value())
                );
            }

            return new CustomResponse<>(
                    "User entities fetched successfully.",
                    response,
                    HttpStatus.OK,
                    String.valueOf(HttpStatus.OK.value())
            );

        } catch (NoSuchElementException ex) {
            return new CustomResponse<>(
                    "User not found: " + ex.getMessage(),
                    null,
                    HttpStatus.NOT_FOUND,
                    String.valueOf(HttpStatus.NOT_FOUND.value())
            );

        } catch (IllegalArgumentException ex) {
            return new CustomResponse<>(
                    "Invalid user ID provided: " + ex.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error while fetching user entities: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    public UserProfileResponse getUserFriends(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Outgoing: user → friend
        var outgoing = friendRepository.findByUserUserId(userId);

        // Incoming: friend → user
        var incoming = friendRepository.findByFriendUserId(userId);

        // Combine both
        var all = Stream.concat(outgoing.stream(), incoming.stream())
                .filter(f -> {
                    int s = f.getFriendStatus().getFriendStatusId();
                    return s == 1 || s == 2 || s == 6; // requested, friends, received
                })
                .map(f -> {

                    // Determine the OTHER user
                    var other = f.getUser().getUserId().equals(userId)
                            ? f.getFriend()
                            : f.getUser();

                    return FriendDTO.builder()
                            .friendId(other.getUserId())
                            .friendUsername(other.getUsername())
                            .statusId(f.getFriendStatus().getFriendStatusId())
                            .status(f.getFriendStatus().getStatus())
                            .statusDescription(f.getFriendStatus().getDescription())
                            .build();
                })
                .toList();

        return UserProfileResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .friends(all)
                .build();
    }
    public CustomResponse<ContributionSummaryDTO> getUserContributionSummary(Long userId) {

        var user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new CustomResponse<>(
                    "User not found",
                    HttpStatus.BAD_REQUEST,
                    "400"
            );
        }

        // Get all contributions for this user
        var contributions = contributionRepository.findByUserId(userId);

        // Total NIL coins across all entities
        long totalNilCoins = contributions.stream()
                .mapToLong(c -> c.getCoinsContributed() != null ? c.getCoinsContributed() : 0)
                .sum();

        // GROUP AGGREGATION
        var groupTotals = user.getUserGroups().stream()
                .map(ug -> {
                    long steps = contributions.stream()
                            .filter(c -> c.getEntityId().equals(ug.getGroup().getGroupId()))
                            .mapToLong(c -> c.getStepsContributed() != null ? c.getStepsContributed() : 0)
                            .sum();

                    long nil = contributions.stream()
                            .filter(c -> c.getEntityId().equals(ug.getGroup().getGroupId()))
                            .mapToLong(c -> c.getCoinsContributed() != null ? c.getCoinsContributed() : 0)
                            .sum();

                    return GroupContributionDTO.builder()
                            .groupId(ug.getGroup().getGroupId())
                            .groupName(ug.getGroup().getName())
                            .totalSteps(steps)
                            .totalNilCoins(nil)
                            .build();
                })
                .toList();

        // ENTITY AGGREGATION
        var entityTotals = user.getUserEntities().stream()
                .map(ue -> {
                    long steps = contributions.stream()
                            .filter(c -> c.getEntityId().equals(ue.getEntity().getEntityId()))
                            .mapToLong(c -> c.getStepsContributed() != null ? c.getStepsContributed() : 0)
                            .sum();

                    long nil = contributions.stream()
                            .filter(c -> c.getEntityId().equals(ue.getEntity().getEntityId()))
                            .mapToLong(c -> c.getCoinsContributed() != null ? c.getCoinsContributed() : 0)
                            .sum();

                    return EntityContributionDTO.builder()
                            .entityId(ue.getEntity().getEntityId())
                            .entityName(ue.getEntity().getName())
                            .totalSteps(steps)
                            .totalNilCoins(nil)
                            .build();
                })
                .toList();

        ContributionSummaryDTO summary = ContributionSummaryDTO.builder()
                .userId(userId)
                .totalNilCoins(totalNilCoins)
                .groupContributions(groupTotals)
                .entityContributions(entityTotals)
                .build();

        return new CustomResponse<>(
                summary,
                HttpStatus.OK,
                "200"
        );
    }

    public CustomResponse<UserStatsResponse> getUserStats(Long userId, String startDate, String endDate) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDateTime start = (startDate != null)
                    ? LocalDate.parse(startDate).atStartOfDay()
                    : LocalDate.now().withDayOfMonth(1).atStartOfDay();

            LocalDateTime end = (endDate != null)
                    ? LocalDate.parse(endDate).atTime(23, 59, 59)
                    : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);

            // Total steps and coins for the month
            var contributionTotalDTO = contributionRepository.getUserMonthlyTotals(userId, start, end);

            // Active goals for the month
            var activeGoals = userGoalRepository.findByUserUserIdAndIsActiveTrue(userId)
                    .stream()
                    .map(g -> GoalDTO.builder()
                            .goalId(g.getUserGoalId())
                            .goalName(g.getGoalInstance().getGoal().getGoal())
                            .entityName(g.getGoalInstance().getEntity() != null ? g.getGoalInstance().getEntity().getName() : null)
                            .groupName(g.getGoalInstance().getGroup() != null ? g.getGoalInstance().getGroup().getName() : null)
                            .isActive(true)
                            .build())
                    .toList();

            // Completed goals for the month
            var completedGoals = userGoalRepository.findByUserUserIdAndIsCompleteTrueAndCompletedAtBetween(userId, start, end)
                    .stream()
                    .map(g -> GoalDTO.builder()
                            .goalId(g.getUserGoalId())
                            .goalName(g.getGoalInstance().getGoal().getGoal())
                            .entityName(g.getGoalInstance().getEntity() != null ? g.getGoalInstance().getEntity().getName() : null)
                            .groupName(g.getGoalInstance().getGroup() != null ? g.getGoalInstance().getGroup().getName() : null)
                            .isActive(false)
                            .build())
                    .toList();

            // Achievements (with times earned)
            var achievements = userAchievementRepository.getUserAchievementsWithCount(userId, start, end)
                    .stream()
                    .map(a -> AchievementDTO.builder()
                            .achievementId(a.getAchievementId())
                            .achievementName(a.getAchievementName())
                            .timesEarned(a.getTimesEarned())
                            .entityName(a.getEntityName())
                            .groupName(a.getGroupName())
                            .earnedAt(a.getEarnedAt())
                            .lastEarnedAt(a.getLastEarnedAt())
                            .build())
                    .toList();


            var response = UserStatsResponse.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .totalSteps(contributionTotalDTO.getTotalSteps())
                    .totalCoins(contributionTotalDTO.getTotalNilCoins())
                    .activeGoals(activeGoals)
                    .completedGoals(completedGoals)
                    .achievements(achievements)
                    .build();

            return new CustomResponse<>(
                    "User stats fetched successfully.",
                    response,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error fetching user stats: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }
    public CustomResponse<UserStatsSummaryDTO> getUserStatsByPeriod(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String periodType
    ) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Contribution> contributions =
                    contributionRepository.findByUserIdAndDateRange(
                            userId,
                            startDate.atStartOfDay(),
                            endDate.plusDays(1).atStartOfDay()
                    );
            PeriodKey periodKey = new PeriodKey();
            // Group contributions by period
            Map<String, List<Contribution>> grouped = contributions.stream()
                    .collect(Collectors.groupingBy(c -> periodKey.getPeriodKey(c.getCreatedAt(), periodType)));

            // Build per-period stats
            List<UserPeriodStatsDTO> periodStats = grouped.entrySet().stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        List<Contribution> list = entry.getValue();

                        long totalSteps = list.stream().mapToLong(Contribution::getStepsContributed).sum();
                        long totalCoins = list.stream().mapToLong(Contribution::getCoinsContributed).sum();

                        return UserPeriodStatsDTO.builder()
                                .period(key)
                                .totalSteps(totalSteps)
                                .totalCoins(totalCoins)
                                .build();
                    })
                    .sorted(Comparator.comparing(UserPeriodStatsDTO::getPeriod))
                    .toList();

            // Global averages across periods
            long totalStepsAll = periodStats.stream().mapToLong(UserPeriodStatsDTO::getTotalSteps).sum();
            long totalCoinsAll = periodStats.stream().mapToLong(UserPeriodStatsDTO::getTotalCoins).sum();
            int numberOfPeriods = periodStats.size();

            double avgStepsPerPeriod = numberOfPeriods == 0 ? 0 : (double) totalStepsAll / numberOfPeriods;
            double avgCoinsPerPeriod = numberOfPeriods == 0 ? 0 : (double) totalCoinsAll / numberOfPeriods;

            UserStatsSummaryDTO summary = UserStatsSummaryDTO.builder()
                    .avgSteps(avgStepsPerPeriod)
                    .avgCoins(avgCoinsPerPeriod)
                    .periodStats(periodStats)
                    .build();

            return new CustomResponse<>(
                    "User stats fetched successfully.",
                    summary,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error while fetching stats: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }




//public UserProfileResponse getUserAchievements(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .achievements(user.getUserAchievements().stream().toList())
//            .build();
//}
//public UserProfileResponse getUserGoals(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .goals(user.getUserGoals().stream().toList())
//            .build();
//}
//public UserProfileResponse getUserCompletedGoals(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    var completedGoals = user.getUserGoals()
//            .stream()
//            .filter(g -> Boolean.TRUE.equals(g.getIsComplete()))
//            .toList();
//
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .goals(completedGoals)
//            .build();
//}
//public UserProfileResponse getUserIncompletedGoals(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    var incompletedGoals = user.getUserGoals()
//            .stream()
//            .filter(g -> Boolean.FALSE.equals(g.getIsComplete()))
//            .toList();
//
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .goals(incompletedGoals)
//            .build();
//}
////    public UserProfileResponse getUserGoalHistory(Long userId) {
////
////        var user = userRepository.findById(userId)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        return UserProfileResponse.builder()
////                .userId(userId)
////                .username(user.getUsername())
////                .goalHistory(user.getUserGoalHistories().stream().toList())
////                .build();
////    }
//public UserProfileResponse getUserGroups(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .groups(user.getUserGroups().stream().toList())
//            .build();
//}
//
//public UserProfileResponse getUserEntities(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .entities(user.getUserEntities().stream().toList())
//            .build();
//}
//public UserProfileResponse getUserFriends(Long userId) {
//
//    var user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    return UserProfileResponse.builder()
//            .userId(userId)
//            .username(user.getUsername())
//            .friends(user.getUserFriends().stream().toList())
//            .build();
//}



}

