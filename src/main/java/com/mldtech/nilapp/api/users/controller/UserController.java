package com.mldtech.nilapp.api.users.controller;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.service.UserEntityService;
import com.mldtech.nilapp.api.users.dto.UpdateUserEntitiesRequest;
import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.users.service.UserService;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserEntityService userEntityService;

    @GetMapping("/{userId}/profile")
    public CustomResponse<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
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



}
