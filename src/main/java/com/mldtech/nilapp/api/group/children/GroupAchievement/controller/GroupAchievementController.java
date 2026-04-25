//package com.mldtech.nilapp.api.group.children.GroupAchievement.controller;
//
//import com.mldtech.nilapp.api.group.children.GroupAchievement.service.GroupAchievementService;
//import com.mldtech.nilapp.api.group.children.GroupAchievement.model.GroupAchievement;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/group-achievements")
//@RequiredArgsConstructor
//public class GroupAchievementController {
//
//    private final GroupAchievementService service;
//
//    @GetMapping("/{groupId}")
//    public List<GroupAchievement> getAchievementsForGroup(@PathVariable Long groupId) {
//        return service.getAchievementsForGroup(groupId);
//    }
//
//    @PostMapping("/{groupId}/{achievementId}")
//    public GroupAchievement addGroupAchievement(
//            @PathVariable Long groupId,
//            @PathVariable Long achievementId
//    ) {
//        return service.addGroupAchievement(groupId, achievementId);
//    }
//
//    @DeleteMapping("/{groupId}/{achievementId}")
//    public void removeGroupAchievement(
//            @PathVariable Long groupId,
//            @PathVariable Long achievementId
//    ) {
//        service.removeGroupAchievement(groupId, achievementId);
//    }
//}
