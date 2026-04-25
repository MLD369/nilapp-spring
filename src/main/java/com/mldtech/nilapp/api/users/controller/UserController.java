package com.mldtech.nilapp.api.users.controller;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.dto.UserProfileResponse;
import com.mldtech.nilapp.api.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
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
    @GetMapping("/{userId}/goal-history") //TODO new dto
    public UserProfileResponse getUserGoalHistory(@PathVariable Long userId) {
        return userService.getUserGoalHistory(userId);
    }
    @GetMapping("/{userId}/groups") //TODO new dto
    public UserProfileResponse getUserGroups(@PathVariable Long userId) {
        return userService.getUserGroups(userId);
    }



}
