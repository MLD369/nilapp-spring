package com.mldtech.nilapp.api.users.controller;

import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.service.ContributionService;
import com.mldtech.nilapp.api.entities.service.EntityService;
import com.mldtech.nilapp.api.group.service.GroupService;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.service.UserEntityService;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.api.users.service.UserService;
import com.mldtech.nilapp.helper.CustomResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserEntityService userEntityService;
    private final ContributionService contributionService;
    private final EntityService entityService;
    private final GroupService groupService;

    /**
     * Retrieves the full user profile including:
     * - basic user info
     * - entities
     * - groups
     * - achievements
     * - goals
     * - friends
     *
     * @param userId ID of the user
     * @return CustomResponse containing UserProfileResponse
     */
    @GetMapping("/{userId}/profile")
    public CustomResponse<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    /**
     * Retrieves a summary of a user's contributions:
     * - total coins
     * - total steps
     * - total contributions
     *
     * @param userId ID of the user
     * @return CustomResponse containing ContributionSummaryDTO
     */
    @GetMapping("/{userId}/contributions/summary")
    public CustomResponse<ContributionSummaryDTO> getUserContributionSummary(
            HttpServletRequest request,
            @PathVariable Long userId
    ) {
        return userService.getUserContributionSummary(userId);
    }

    /**
     * Retrieves contributions filtered by:
     * - entityId (optional)
     * - groupId (optional)
     *
     * @param userId ID of the user
     * @param entityId optional entity filter
     * @param groupId optional group filter
     * @return CustomResponse containing list of ContributionDTO
     */
    @GetMapping("/{userId}/contributions/filter")
    public CustomResponse<List<ContributionDTO>> getUserContributionsByFilter(
            @PathVariable Long userId,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) Long groupId
    ) {
        return contributionService.getUserContributionsByFilter(userId, entityId, groupId);
    }

    /**
     * Retrieves all achievements for a user.
     */
    @GetMapping("/{userId}/achievements")
    public UserProfileResponse getUserAchievements(@PathVariable Long userId) {
        return userService.getUserAchievements(userId);
    }

    /**
     * Retrieves all goals for a user.
     */
    @GetMapping("/{userId}/goals")
    public UserProfileResponse getUserGoals(@PathVariable Long userId) {
        return userService.getUserGoals(userId);
    }

    /**
     * Retrieves all completed goals for a user.
     */
    @GetMapping("/{userId}/goals/completed")
    public CustomResponse<UserProfileResponse> getUserCompletedGoals(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return userService.getUserCompletedGoals(userId, startDate, endDate);
    }


    /**
     * Retrieves all incomplete goals for a user.
     */
    @GetMapping("/{userId}/goals/incompleted")
    public UserProfileResponse getUserIncompletedGoals(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return userService.getUserIncompletedGoals(userId, startDate, endDate);
    }

    /**
     * Retrieves only the entities a user is currently a member of.
     *
     * @param userId ID of the user
     * @return CustomResponse containing UserProfileResponse with entity list
     */
    @GetMapping("/{userId}/entities")
    public CustomResponse<UserProfileResponse> getUserEntities(@PathVariable Long userId) {
        return userService.getUserEntities(userId);
    }

    /**
     * Retrieves:
     * - all entities in the system
     * - marks each with joined = true/false depending on user membership
     *
     * Used for the "Entities" tab in the UI.
     */
    @GetMapping("/{userId}/entities/all")
    public CustomResponse<List<EntityDTO>> getAllEntitiesWithUserStatus(@PathVariable Long userId) {
        return entityService.getAllEntitiesWithUserStatus(userId);
    }

    /**
     * Retrieves all friends for a user.
     */
    @GetMapping("/{userId}/friends")
    public UserProfileResponse getUserFriends(@PathVariable Long userId) {
        return userService.getUserFriends(userId);
    }

    /**
     * Bulk update of user entities.
     * Replaces all existing user entities with the provided list.
     *
     * @param userId ID of the user
     * @param request contains list of entity IDs
     */
    @PutMapping("/{userId}/entities")
    public List<UserEntity> updateUserEntities(
            @PathVariable Long userId,
            @RequestBody UpdateUserEntitiesRequest request
    ) {
        return userEntityService.setUserEntities(userId, request.getEntityIds());
    }

    /**
     * Adds a single entity to a user.
     * (Legacy endpoint — replaced by joinEntity)
     */
    @PostMapping("/{userId}/entities/{entityId}")
    public UserEntity addEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        return userEntityService.addEntity(userId, entityId);
    }

    /**
     * Removes a single entity from a user.
     * (Legacy endpoint — replaced by leaveEntity)
     */
    @DeleteMapping("/{userId}/entities/{entityId}")
    public void removeEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        userEntityService.removeEntity(userId, entityId);
    }

    /**
     * Retrieves contribution statistics for a user over a period.
     * Supports filtering by:
     * - entity
     * - group
     * - period (daily, weekly, monthly, yearly)
     * - status (completed, pending)
     * - date range
     */
    @GetMapping("/{userId}/contribution-stats")
    public CustomResponse<List<ContributionPeriodStatsDTO>> getContributionStats(
            HttpServletRequest request,
            @PathVariable Long userId,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) Long groupId,
            @RequestParam String period,
            @RequestParam String status,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        return contributionService.getContributionStats(
                userId,
                entityId,
                groupId,
                period,
                status,
                start,
                end
        );
    }

    // ---------------------------------------------------------
    // ENTITY JOIN / LEAVE (History-based membership)
    // ---------------------------------------------------------

    /**
     * User joins an entity.
     * Creates a new membership row with joined_at timestamp.
     */
    @PostMapping("/{userId}/entities/{entityId}/join")
    public CustomResponse<String> joinEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        return entityService.joinEntity(userId, entityId);
    }

    /**
     * User leaves an entity.
     * Updates left_at timestamp instead of deleting the row.
     */
    @PutMapping("/{userId}/entities/{entityId}/leave")
    public CustomResponse<String> leaveEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        return entityService.leaveEntity(userId, entityId);
    }

    // ---------------------------------------------------------
    // GROUP JOIN / LEAVE (History-based membership)
    // ---------------------------------------------------------

    /**
     * User joins a group.
     * Creates a new membership row with joined_at timestamp.
     */
    @PostMapping("/{userId}/groups/{groupId}/join")
    public CustomResponse<String> joinGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        return groupService.joinGroup(userId, groupId);
    }

    /**
     * User leaves a group.
     * Updates left_at timestamp instead of deleting the row.
     */
    @PutMapping("/{userId}/groups/{groupId}/leave")
    public CustomResponse<String> leaveGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        return groupService.leaveGroup(userId, groupId);
    }

    // ---------------------------------------------------------
    // GROUP LISTING
    // ---------------------------------------------------------

    /**
     * Retrieves only the groups a user is currently a member of.
     */
    @GetMapping("/{userId}/groups")
    public UserProfileResponse getUserGroups(@PathVariable Long userId) {
        return userService.getUserGroups(userId);
    }

    /**
     * Retrieves:
     * - all groups in the system
     * - marks each with joined = true/false depending on user membership
     *
     * Used for the "Groups" tab in the UI.
     */
    @GetMapping("/{userId}/groups/all")
    public CustomResponse<List<GroupDTO>> getAllGroups(@PathVariable Long userId) {
        return groupService.getAllGroupsWithUserStatus(userId);
    }

    @GetMapping("/{userId}/stats")
    public CustomResponse<UserStatsResponse> getUserStats( /// for the stats page
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return userService.getUserStats(userId, startDate, endDate);
    }


    @GetMapping("/{userId}/stats-by-period")
    public CustomResponse<UserStatsSummaryDTO> getUserStatsByPeriod(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String period
    ) {
        try {
            // Validate period
            String normalizedPeriod = period.toUpperCase();
            if (!List.of("DAILY", "WEEKLY", "MONTHLY", "YEARLY").contains(normalizedPeriod)) {
                return new CustomResponse<>(
                        "Invalid period. Must be DAILY, WEEKLY, MONTHLY, or YEARLY.",
                        null,
                        HttpStatus.BAD_REQUEST,
                        "400"
                );
            }

            // Parse dates
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (end.isBefore(start)) {
                return new CustomResponse<>(
                        "endDate cannot be before startDate.",
                        null,
                        HttpStatus.BAD_REQUEST,
                        "400"
                );
            }

            // Call service
            return userService.getUserStatsByPeriod(
                    userId,
                    start,
                    end,
                    normalizedPeriod
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





    @GetMapping("/{userId}/goal-dashboard")
    public CustomResponse<UserGoalDashboardResponse> getDashboard(@PathVariable Long userId) {
        try {
            UserGoalDashboardResponse dashboard = userService.getUserGoalDashboard(userId);

            return new CustomResponse<>(
                            "Goal dashboard loaded successfully",
                            dashboard,
                            HttpStatus.OK,
                            "200"
                    );

        } catch (EntityNotFoundException ex) {
            return new CustomResponse<>(
                            ex.getMessage(),
                            null,
                            HttpStatus.NOT_FOUND,
                            "404"
                    );

        } catch (Exception ex) {
            return new CustomResponse<>(
                            "Failed to load goal dashboard",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "500"
                    );
        }
    }


}
