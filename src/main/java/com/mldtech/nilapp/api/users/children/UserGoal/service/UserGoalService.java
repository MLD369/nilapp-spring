package com.mldtech.nilapp.api.users.children.UserGoal.service;


//import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGoalService {

    private final UserGoalRepository repo;
    private final UserRepository userRepo;
//    private final GoalRepository goalRepo;

    public List<UserGoal> getActiveGoals(Long userId) {
        return repo.findByUserUserIdAndIsActiveTrue(userId);
    }

//    public UserGoal assignGoal(Long userId, Long goalId, Integer allocationPct) {
//
//        UserGoal ug = UserGoal.builder()
//                .user(userRepo.getReferenceById(userId))
////                .goal(goalRepo.getReferenceById(goalId))
//                .allocationPct(BigDecimal.valueOf(allocationPct))
//                .isActive(true)
//                .build();
//
//        return repo.save(ug);
//    }

//    public void deactivateGoal(Long userGoalId) {
//        UserGoal ug = repo.findById(userGoalId)
//                .orElseThrow(() -> new RuntimeException("Goal not found"));
//
//        ug.setIsActive(false);
//        repo.save(ug);
//    }
}
