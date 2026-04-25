//package com.mldtech.nilapp.api.group.children.GroupGoalHistory.controller;
//
//import com.mldtech.nilapp.api.group.children.GroupGoalHistory.model.GroupGoalHistory;
//import com.mldtech.nilapp.api.group.children.GroupGoalHistory.service.GroupGoalHistoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/group-goal-history")
//@RequiredArgsConstructor
//public class GroupGoalHistoryController {
//
//    private final GroupGoalHistoryService service;
//
//    @GetMapping("/group/{groupId}")
//    public List<GroupGoalHistory> getHistoryForGroup(@PathVariable Long groupId) {
//        return service.getHistoryForGroup(groupId);
//    }
//
//    @GetMapping("/goal/{groupGoalId}")
//    public List<GroupGoalHistory> getHistoryForGroupGoal(@PathVariable Long groupGoalId) {
//        return service.getHistoryForGroupGoal(groupGoalId);
//    }
//
//    @PostMapping
//    public GroupGoalHistory addHistoryEntry(@RequestBody GroupGoalHistory history) {
//        return service.addHistoryEntry(history);
//    }
//
//    @PutMapping("/{historyId}/close")
//    public GroupGoalHistory closeHistoryEntry(@PathVariable Long historyId) {
//        return service.closeHistoryEntry(historyId);
//    }
//}
//
