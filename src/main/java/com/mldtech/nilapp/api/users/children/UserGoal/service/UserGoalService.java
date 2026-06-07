package com.mldtech.nilapp.api.users.children.UserGoal.service;


//import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.api.goals.children.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final UserRepository userRepository;
    private final GoalInstanceRepository goalInstanceRepository;

//    private final GoalRepository goalRepo;

    public List<UserGoal> getActiveGoals(Long userId) {
        return userGoalRepository.findByUserUserIdAndIsActiveTrue(userId);
    }


    public CustomResponse<?> joinGoal(Long userId, Long instanceId, UserGoal userGoal) {

        // Prevent duplicate join
        boolean alreadyJoined = userGoalRepository.existsByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(
                userId, instanceId
        );

        if (alreadyJoined) {
            return new CustomResponse<>(
                    "User already joined this goal",
                    HttpStatus.BAD_REQUEST,
                    "400"
            );
        }

        userGoal.setUser(userRepository.getReferenceById(userId));
        userGoal.setGoalInstance(goalInstanceRepository.getReferenceById(instanceId));
        userGoal.setJoinedAt(LocalDateTime.from(Instant.now()));
        userGoal.setLeftAt(null); // ensure active

        return new CustomResponse<>(userGoalRepository.save(userGoal), HttpStatus.OK, "200");
    }
    public CustomResponse<?> leaveGoal(Long userId, Long instanceId) {

        UserGoal userGoal = userGoalRepository
                .findByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(userId, instanceId)
                .orElseThrow(() -> new RuntimeException("User is not currently joined to this goal"));

        userGoal.setLeftAt(LocalDateTime.now());

        return new CustomResponse<>(userGoalRepository.save(userGoal), HttpStatus.OK, "200");
    }


    public CustomResponse<?> getAll() {
        return new CustomResponse<>(userGoalRepository.findAll(), HttpStatus.OK, "200");
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
