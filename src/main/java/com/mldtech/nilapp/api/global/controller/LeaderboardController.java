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

    // TODO add param to say how many users to get in the response this will need conditions in the service
    // by city,state,gender,age group (separate by ranges?), entity,group, sports conference,region,
    //
    //     //TODO add device device(ios,android1) maybe should list per device and overall. maybe go deeper by watch type or brand or provider
    @GetMapping("/detailed")
    public CustomResponse<DetailedLeaderboardResponse> getDetailedLeaderboard(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String leaderboardCategory,
            @RequestParam String filterType,
            @RequestParam String filterString,
            @RequestParam(defaultValue = "25") Integer limit
    ) {
    // PRINT EVERYTHING
            System.out.println("=== Detailed Leaderboard paarams ===");
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Category: " + leaderboardCategory);
            System.out.println("Filter Type: " + filterType);
            System.out.println("Filter String: " +filterString);
            System.out.println("Limit: " + limit);

            System.out.println("====================================");
        DetailedLeaderboardResponse response = leaderboardService.getLeaderboardFilteredRanks(
            startDate,
            endDate,
            leaderboardCategory,
            filterType,
            filterString,
            limit
    );
        // PRINT EVERYTHING
        System.out.println("=== Detailed Leaderboard Response ===");
        System.out.println("Start Date: " + response.getStartDate());
        System.out.println("End Date: " + response.getEndDate());
        System.out.println("Category: " + response.getLeaderboardCategory());
        System.out.println("Filter Type: " + response.getFilterType());
        System.out.println("Filter String: " + response.getFilterString());
        System.out.println("Limit: " + response.getLimit());
        System.out.println("Results Count: " + (response.getResults() != null ? response.getResults().size() : 0));
        System.out.println("Results: " + response.getResults());
        System.out.println("====================================");
        return new CustomResponse<>(response, HttpStatus.OK, "200");
    }

}
