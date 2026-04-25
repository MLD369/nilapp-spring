package com.mldtech.nilapp.api.users.children.UserAchievement.service;

//import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
//import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserAchievement.repository.UserAchievementRepository;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAchievementService {

    private final UserAchievementRepository repo;
//    private final UserRepository userRepo;
////    private final AchievementRepository achievementRepo;
//
    public List<UserAchievement> getAchievements(Long userId) {
        return repo.findUserAchievementByUserUserId(userId);
    }

//    public UserAchievement addAchievement(Long userId, Long achievementId) {
//
//        if (repo.existsByUserUserIdAndAchievementId(userId, achievementId)) {
//            throw new RuntimeException("User already has this achievement");
//        }
//
//        UserAchievement ua = UserAchievement.builder()
//                .user(userRepo.getReferenceById(userId))
////                .achievement(achievementRepo.getReferenceById(achievementId))
//                .earnedAt(LocalDate.now())
//                .build();
//
//        return repo.save(ua);
//    }
}


