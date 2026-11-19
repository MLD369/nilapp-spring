package com.mldtech.nilapp.api.daily_stat.controller;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import com.mldtech.nilapp.api.daily_stat.service.DailyStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@RestController
@RequestMapping("/api/v1/daily-stats")
@RequiredArgsConstructor
public class DailyStatController {

    private final DailyStatService service;

    @GetMapping("/{userId}")
    public List<DailyStat> getStatsForUser(@AuthenticationPrincipal Long userId) {
        return service.getStatsForUser(userId);
    }

    @GetMapping("/{userId}/{date}")
    public DailyStat getStatForDay(
            @AuthenticationPrincipal Long userId,
            @PathVariable String date
    ) {
        return service.getStatForDay(userId, LocalDate.parse(date));
    }

    @GetMapping("/{userId}/range")
    public List<DailyStat> getStatsForRange(
            @AuthenticationPrincipal Long userId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        return service.getStatsForRange(userId, LocalDate.parse(start), LocalDate.parse(end));
    }

    @PostMapping
    public DailyStat createOrUpdateDailyStat(@RequestBody DailyStat stat) {
        return service.createOrUpdateDailyStat(stat);
    }

    @PostMapping("/{userId}/total-device-steps")
    public DailyStat syncYesterdayDeviceSteps(@AuthenticationPrincipal Long userId,@RequestBody DailyStat stat) {
        return service.syncYesterdayDeviceSteps(userId,stat.getTotalDeviceSteps());
    }
}

