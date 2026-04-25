package com.mldtech.nilapp.api.users.children.UserAchievement.controller;

//import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
//import com.mldtech.nilapp.api.users.children.UserAchievement.service.UserAchievementService;

import com.mldtech.nilapp.api.users.children.UserAchievement.service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-achievements")
@RequiredArgsConstructor
public class UserAchievementController {

    private final UserAchievementService service;


}
