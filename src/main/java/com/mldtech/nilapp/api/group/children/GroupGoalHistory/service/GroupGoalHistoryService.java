//package com.mldtech.nilapp.api.group.children.GroupGoalHistory.service;
//
//import com.mldtech.nilapp.api.group.children.GroupGoalHistory.model.GroupGoalHistory;
//import com.mldtech.nilapp.api.group.children.GroupGoalHistory.repository.GroupGoalHistoryRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class GroupGoalHistoryService {
//
//    private final GroupGoalHistoryRepository repository;
//
//    public List<GroupGoalHistory> getHistoryForGroup(Long groupId) {
//        return repository.findByGroupId(groupId);
//    }
//
//    public List<GroupGoalHistory> getHistoryForGroupGoal(Long groupGoalId) {
//        return repository.findByGroupGoalId(groupGoalId);
//    }
//
//    public GroupGoalHistory addHistoryEntry(GroupGoalHistory history) {
//        history.setJoinedAt(LocalDateTime.now());
//        return repository.save(history);
//    }
//
//    public GroupGoalHistory closeHistoryEntry(Long historyId) {
//        GroupGoalHistory h = repository.findById(historyId)
//                .orElseThrow(() -> new RuntimeException("History entry not found"));
//
//        h.setLeftAt(LocalDateTime.now());
//        return repository.save(h);
//    }
//}
//
