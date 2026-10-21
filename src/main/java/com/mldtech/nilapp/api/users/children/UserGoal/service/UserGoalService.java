package com.mldtech.nilapp.api.users.children.UserGoal.service;


//import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.api.goals.children.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.users.children.UserGoal.model.UserGoal;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.children.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.users.dto.UserGoalDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import jakarta.transaction.Transactional;
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


    public CustomResponse<?> joinGoal(Long userId, Long instanceId) {

        // Prevent duplicate join
        boolean alreadyJoined = userGoalRepository
                .existsByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(userId, instanceId);

        if (alreadyJoined) {
            return new CustomResponse<>(
                    "User already joined this goal",
                    HttpStatus.BAD_REQUEST,
                    "400"
            );
        }

        UserGoal userGoal = new UserGoal();

        userGoal.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));

        userGoal.setGoalInstance(goalInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Goal instance not found")));

        userGoal.setJoinedAt(LocalDateTime.now());
        userGoal.setLeftAt(null);
        userGoal.setIsActive(true);
        userGoal.setIsComplete(false);
        userGoal.setAllocationPct(BigDecimal.ZERO);

        UserGoal saved = userGoalRepository.save(userGoal);

        // ⭐ Return DTO instead of entity to avoid Hibernate proxy issues
        UserGoalDTO dto = UserGoalDTO.builder()
                .userGoalId(saved.getUserGoalId())
                .userId(userId)
                .goalInstanceId(instanceId)
                .joinedAt(saved.getJoinedAt())
                .leftAt(saved.getLeftAt())
                .allocationPct(saved.getAllocationPct())
                .isActive(saved.getIsActive())
                .isComplete(saved.getIsComplete())
                .build();

        return new CustomResponse<>(dto, HttpStatus.OK, "200");
    }


    public CustomResponse<?> leaveGoal(Long userId, Long instanceId) {

        UserGoal userGoal = userGoalRepository
                .findByUser_UserIdAndGoalInstance_GoalInstanceIdAndLeftAtIsNull(userId, instanceId)
                .orElseThrow(() -> new RuntimeException("User is not currently joined to this goal"));

        userGoal.setLeftAt(LocalDateTime.now());
        userGoal.setIsActive(false);

        UserGoal saved = userGoalRepository.save(userGoal);

        // ⭐ Return DTO instead of entity to avoid Hibernate proxy issues
        UserGoalDTO dto = UserGoalDTO.builder()
                .userGoalId(saved.getUserGoalId())
                .userId(userId)
                .goalInstanceId(instanceId)
                .joinedAt(saved.getJoinedAt())
                .leftAt(saved.getLeftAt())
                .allocationPct(saved.getAllocationPct())
                .isActive(saved.getIsActive())
                .isComplete(saved.getIsComplete())
                .build();

        return new CustomResponse<>(dto, HttpStatus.OK, "200");
    }



    public CustomResponse<?> getAll() {
        return new CustomResponse<>(userGoalRepository.findAll(), HttpStatus.OK, "200");
    }

    /**
     * Executes the manual rejoin logic inside a transactional boundary.
     * Throws an exception if the user already has an active instance running.
     */
    @Transactional
    public void rejoinGoalChallenge(Long userId, Long goalInstanceId, String targetType, int targetAmount, double allocationPct) {
        int result = userGoalRepository.manuallyRejoinGoalChallenge(
                userId, goalInstanceId, targetType, targetAmount, allocationPct
        );

        if (result == 0) {
            throw new IllegalStateException("ALREADY_ACTIVE: You are already actively participating in this goal challenge instance.");
        }
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
