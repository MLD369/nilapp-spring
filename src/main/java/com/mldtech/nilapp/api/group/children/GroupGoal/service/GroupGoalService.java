//package com.mldtech.nilapp.api.group.children.GroupGoal.service;
//
//import com.mldtech.nilapp.api.group.children.GroupGoal.repository.GroupGoalRepository;
//import com.mldtech.nilapp.api.group.children.GroupGoal.model.GroupGoal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class GroupGoalService {
//
//    private final GroupGoalRepository repository;
//
//    public List<GroupGoal> getGoalsForGroup(Long groupId) {
//        return repository.findByGroupId(groupId);
//    }
//
//    public GroupGoal addGroupGoal(GroupGoal goal) {
//        goal.setJoinedAt(LocalDateTime.now());
//        goal.setIsActive(true);
//        return repository.save(goal);
//    }
//
//    public GroupGoal deactivateGroupGoal(Long groupGoalId) {
//        GroupGoal gg = repository.findById(groupGoalId)
//                .orElseThrow(() -> new RuntimeException("Group goal not found"));
//
//        gg.setIsActive(false);
//        gg.setLeftAt(LocalDateTime.now());
//
//        return repository.save(gg);
//    }
//}
//
