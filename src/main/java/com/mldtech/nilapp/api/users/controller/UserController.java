package com.mldtech.nilapp.api.users.controller;

import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.service.ContributionService;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.service.UserEntityService;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.dto.ContributionPeriodStatsDTO;
import com.mldtech.nilapp.api.users.dto.UpdateUserEntitiesRequest;
import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.users.service.UserService;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{userId}/profile")
    public CustomResponse<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }
    @GetMapping("/{userId}/contributions/summary")
    public CustomResponse<ContributionSummaryDTO> getUserContributionSummary(@PathVariable Long userId) {
        return userService.getUserContributionSummary(userId);
    }

    @GetMapping("/{userId}/contributions/filter")
    public CustomResponse<List<ContributionDTO>> getUserContributionsByFilter(
            @PathVariable Long userId,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) Long groupId
    ) {
        return contributionService.getUserContributionsByFilter(userId, entityId, groupId);
    }

    @GetMapping("/{userId}/achievements") //TODO new dto
    public UserProfileResponse getUserAchievements(@PathVariable Long userId) {
        return userService.getUserAchievements(userId);
    }
    @GetMapping("/{userId}/goals") //TODO new dto
    public UserProfileResponse getUserGoals(@PathVariable Long userId) {
        return userService.getUserGoals(userId);
    }
    @GetMapping("/{userId}/goals/completed") //TODO new dto
    public UserProfileResponse getUserCompletedGoals(@PathVariable Long userId) {
        return userService.getUserCompletedGoals(userId);
    }
    @GetMapping("/{userId}/goals/incompleted") //TODO new dto
    public UserProfileResponse getUserIncompletedGoals(@PathVariable Long userId) {
        return userService.getUserIncompletedGoals(userId);
    }
//    @GetMapping("/{userId}/goal-history") //TODO new dto
//    public UserProfileResponse getUserGoalHistory(@PathVariable Long userId) {
//        return userService.getUserGoalHistory(userId);
//    }
    @GetMapping("/{userId}/groups") //TODO new dto
    public UserProfileResponse getUserGroups(@PathVariable Long userId) {
        return userService.getUserGroups(userId);
    }
    @GetMapping("/{userId}/entities") //TODO new dto
    public UserProfileResponse getUserEntities(@PathVariable Long userId) {
        return userService.getUserEntities(userId);
    }
    @GetMapping("/{userId}/friends") //TODO new dto
    public UserProfileResponse getUserFriends(@PathVariable Long userId) {
        return userService.getUserFriends(userId);
    }

    @PutMapping("/{userId}/entities")
    public List<UserEntity> updateUserEntities(
            @PathVariable Long userId,
            @RequestBody UpdateUserEntitiesRequest request
    ) {
        return userEntityService.setUserEntities(userId, request.getEntityIds());
    }



    // ADD ENTITY
    @PostMapping("/{userId}/entities/{entityId}")
    public UserEntity addEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        return userEntityService.addEntity(userId, entityId);
    }

    // REMOVE ENTITY
    @DeleteMapping("/{userId}/entities/{entityId}")
    public void removeEntity(
            @PathVariable Long userId,
            @PathVariable Long entityId
    ) {
        userEntityService.removeEntity(userId, entityId);
    }

    @GetMapping("/{userId}/contribution-stats")
    public CustomResponse<List<ContributionPeriodStatsDTO>> getContributionStats(
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



}
