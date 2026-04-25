//package com.mldtech.nilapp.api.users.children.UserGoalHistory.service;
//
//
////import com.mldtech.nilapp.api.goals.repository.GoalRepository;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.model.UserGoalHistory;
//import com.mldtech.nilapp.api.users.children.UserGoalHistory.repository.UserGoalHistoryRepository;
//import com.mldtech.nilapp.api.users.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserGoalHistoryService {
//
//    private final UserGoalHistoryRepository repo;
//    private final UserRepository userRepo;
////    private final GoalRepository goalRepo;
//
//    public List<UserGoalHistory> getHistory(Long userId) {
//        return repo.findByUserUserId(userId);
//    }
//
//    public UserGoalHistory recordHistory(
//            Long userId,
//            Long goalId,
//            int coins,
//            int steps
//    ) {
//        UserGoalHistory history = UserGoalHistory.builder()
//                .user(userRepo.getReferenceById(userId))
////                .goal(goalRepo.getReferenceById(goalId))
//                .joinedAt(LocalDateTime.now())
//                .coinsContributed(coins)
//                .stepsContributed(steps)
//                .build();
//
//        return repo.save(history);
//    }
//}
//
//
