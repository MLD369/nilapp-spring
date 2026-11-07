package com.mldtech.nilapp.api.users.children.UserAchievement.controller;

//import com.mldtech.nilapp.api.users.children.UserAchievement.model.UserAchievement;
//import com.mldtech.nilapp.api.users.children.UserAchievement.service.UserAchievementService;

import com.mldtech.nilapp.api.users.children.UserAchievement.service.UserAchievementService;
import com.mldtech.nilapp.api.users.dto.AchievementCountDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-achievements")
@RequiredArgsConstructor
public class UserAchievementController {
    private static final Logger log = LoggerFactory.getLogger(UserAchievementController.class);
    private final UserAchievementService userAchievementService;

    /**
     * Public visibility endpoint for looking up individual achievement metrics.
     * Example endpoint calls:
     *   GET /api/v1/user-achievements?search=fit_tracker_99
     *   GET /api/v1/user-achievements?search=4521
     */
    @GetMapping
    public ResponseEntity<List<AchievementCountDTO>> getTargetUserAchievements(
            @RequestParam("search") String searchToken,
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        log.info("REST endpoint hit: fetching badge statistics using parameter token: {}", searchToken);

        List<AchievementCountDTO> results = userAchievementService.fetchUserAchievements(searchToken, start, end);

        return ResponseEntity.ok(results);
    }

    /**
     * Public visibility endpoint for looking up individual achievement metrics.
     * Example endpoint calls:
     *   GET /api/v1/user-achievements/count?userId=4521
     *   GET /api/v1/user-achievements/count?userId=4521&start=2026-01-01T00:00:00
     */
    @GetMapping("count")
    public ResponseEntity<List<AchievementCountDTO>> getTargetUserAchievementsCount(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        log.info("REST request to fetch achievement badge counts for User ID: {}", userId);

        // Fallbacks matching your query date criteria
        LocalDateTime searchStart = (start != null) ? start : LocalDateTime.now().minusYears(10);
        LocalDateTime searchEnd = (end != null) ? end : LocalDateTime.now();

        List<AchievementCountDTO> results = userAchievementService.getUserAchievementsWithCount(userId, searchStart, searchEnd);

        return ResponseEntity.ok(results);
    }
}
