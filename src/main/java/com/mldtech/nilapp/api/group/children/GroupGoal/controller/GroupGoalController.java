//package com.mldtech.nilapp.api.group.children.GroupGoal.controller;
//
//import com.mldtech.nilapp.api.group.children.GroupGoal.service.GroupGoalService;
//import com.mldtech.nilapp.api.group.children.GroupGoal.model.GroupGoal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/group-goals")
//@RequiredArgsConstructor
//public class GroupGoalController {
//
//    private final GroupGoalService service;
//
//    @GetMapping("/{groupId}")
//    public List<GroupGoal> getGoalsForGroup(@PathVariable Long groupId) {
//        return service.getGoalsForGroup(groupId);
//    }
//
//    @PostMapping
//    public GroupGoal addGroupGoal(@RequestBody GroupGoal goal) {
//        return service.addGroupGoal(goal);
//    }
//
//    @PutMapping("/{groupGoalId}/deactivate")
//    public GroupGoal deactivateGroupGoal(@PathVariable Long groupGoalId) {
//        return service.deactivateGroupGoal(groupGoalId);
//    }
//}
