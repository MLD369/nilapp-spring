package com.mldtech.nilapp.api.global.controller;

import com.mldtech.nilapp.api.global.service.LeaderboardService;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/steps")
    public ResponseEntity<CustomResponse<List<StepLeaderDTO>>> getTopSteps(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        var data = leaderboardService.getLeaderboard(startDate, endDate).getStepLeaders();
        return ResponseEntity.ok(new CustomResponse<>(data, HttpStatus.OK, "200"));
    }

    @GetMapping("/coins")
    public ResponseEntity<CustomResponse<List<CoinLeaderDTO>>> getTopCoins(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        var data = leaderboardService.getLeaderboard(startDate, endDate).getCoinLeaders();
        return ResponseEntity.ok(new CustomResponse<>(data, HttpStatus.OK, "200"));
    }

    @GetMapping("/streaks")
    public ResponseEntity<CustomResponse<List<StreakLeaderDTO>>> getTopStreaks() {
        var data = leaderboardService.getTopStreaks();
        return ResponseEntity.ok(new CustomResponse<>(data, HttpStatus.OK, "200"));
    }

    @GetMapping("/entities")
    public ResponseEntity<CustomResponse<List<EntityNilLeaderDTO>>> getEntityNilLeaders(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime startTs = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endTs = LocalDate.parse(endDate).atTime(23, 59, 59);

        var data = leaderboardService.getEntityNilLeaders(startTs, endTs);
        return ResponseEntity.ok(new CustomResponse<>(data, HttpStatus.OK, "200"));
    }
    // TODO only get the top 3 for the user list
    @GetMapping("")
    public ResponseEntity<CustomResponse<GlobalLeaderboardResponse>> getLeaderboard(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        var data = leaderboardService.getLeaderboard(startDate, endDate);
        return ResponseEntity.ok(new CustomResponse<>(data, HttpStatus.OK, "200"));
    }
}
