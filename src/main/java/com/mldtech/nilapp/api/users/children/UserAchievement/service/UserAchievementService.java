package com.mldtech.nilapp.api.users.children.UserAchievement.service;

//import com.mldtech.nilapp.api.achievements.repository.AchievementRepository;
//import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.batches.scheduler.BatchScheduler;
import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
import com.mldtech.nilapp.api.users.children.UserAchievement.repository.UserAchievementRepository;
import com.mldtech.nilapp.api.users.dto.AchievementCountDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAchievementService {
    private static final Logger log = LoggerFactory.getLogger(UserAchievementService.class);
    private final UserAchievementRepository userAchievementRepository;
//    private final UserRepository userRepo;
////    private final AchievementRepository achievementRepo;
//
    public List<UserAchievement> getAchievements(Long userId) {
        return userAchievementRepository.findUserAchievementByUserUserId(userId);
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

    /**
     * Dynamically determines whether to search by User ID or Username token.
     * Automatically applies fallbacks for empty date ranges.
     */
    @Transactional(readOnly = true)
    public List<AchievementCountDTO> fetchUserAchievements(String searchToken, LocalDateTime start, LocalDateTime end) {
        // Enforce baseline defaults if time parameters are omitted
        LocalDateTime searchStart = (start != null) ? start : LocalDateTime.now().minusYears(10);
        LocalDateTime searchEnd = (end != null) ? end : LocalDateTime.now();

        log.info("Processing target user achievements search for: '{}' (Window: {} to {})",
                searchToken, searchStart, searchEnd);

        // Pattern matching: Check if input token consists entirely of numbers
        if (searchToken.matches("\\d+")) {
            Long parsedUserId = Long.parseLong(searchToken);
            log.info("Numeric search token detected. Executing lookup via User ID: {}", parsedUserId);
            return userAchievementRepository.getUserAchievementsWithCount(parsedUserId, searchStart, searchEnd);
        }

        // Default path: Alpha-numeric username lookup string
        log.info("Alphanumeric text detected. Executing lookup via Username: {}", searchToken);
        return userAchievementRepository.getUserAchievementsWithCountByUsername(searchToken, searchStart, searchEnd);
    }

    /**
     * Fetches the aggregated achievement counts and timestamps for a specific User ID.
     */
    @Transactional(readOnly = true)
    public List<AchievementCountDTO> getUserAchievementsWithCount(Long userId, LocalDateTime start, LocalDateTime end) {
        log.info("Processing achievement metric counts for User ID: {} within window: [{} -> {}]",
                userId, start, end);

        return userAchievementRepository.getUserAchievementsWithCount(userId, start, end);
    }

}


