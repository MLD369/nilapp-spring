package com.mldtech.nilapp.api.daily_stat.service;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
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
public class DailyStatService {
    private static final Logger log = LoggerFactory.getLogger(DailyStatService.class);

    private final DailyStatRepository dailyStatRepository;

    public List<DailyStat> getStatsForUser(Long userId) {
        return dailyStatRepository.findByUserId(userId);
    }

    public DailyStat getStatForDay(Long userId, LocalDate date) {
        return dailyStatRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new RuntimeException("Daily stat not found"));
    }

    public List<DailyStat> getStatsForRange(Long userId, LocalDate start, LocalDate end) {
        return dailyStatRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    public DailyStat createOrUpdateDailyStat(DailyStat stat) {

        // If exists, update instead of creating a duplicate
        return dailyStatRepository.findByUserIdAndDate(stat.getUserId(), stat.getDate())
                .map(existing -> {
                    existing.setTotalDeviceSteps(stat.getTotalDeviceSteps());
                    existing.setTotalClaimedSteps(stat.getTotalClaimedSteps());
                    existing.setCoins(stat.getCoins());
                    existing.setDistanceMeters(stat.getDistanceMeters());
                    existing.setDeviceSource(stat.getDeviceSource());
                    existing.setSyncTimestamp(LocalDateTime.now());
                    existing.setCalories(stat.getCalories());
                    return dailyStatRepository.save(existing);
                })
                .orElseGet(() -> {
                    stat.setSyncTimestamp(LocalDateTime.now());
                    return dailyStatRepository.save(stat);
                });
    }

    @Transactional
    public DailyStat syncYesterdayDeviceSteps(Long userId, int deviceSteps) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("Processing device steps synchronization for User ID: {} for date: {}. Steps: {}",
                userId, yesterday, deviceSteps);

        DailyStat stats = dailyStatRepository.findByUserIdAndDate(userId, yesterday)
                .orElseGet(() -> {
                    log.info("No daily stat row found for User ID: {} on {}. Creating new row.", userId, yesterday);
                    DailyStat newRow = new DailyStat();
                    newRow.setUserId(userId);
                    newRow.setDate(yesterday);
                    newRow.setTotalClaimedSteps(0);
                    newRow.setCoins(0);
                    newRow.setLifetimeSyncedDeviceSteps(0);
                    newRow.setLifetimeSyncedClaimedSteps(0);
                    newRow.setLifetimeSyncedCoins(0);
                    return newRow;
                });

        // 📈 Apply the freshly read physical steps
        stats.setTotalDeviceSteps(deviceSteps);

        // ⚡ CRITICAL: Force the flag to false so the streaming engine processes this update immediately!
        stats.setSyncedToLifetime(false);

        DailyStat savedStats = dailyStatRepository.save(stats);
        log.info("Successfully persisted device steps and flagged for lifetime streaming for User ID: {} on date: {}", userId, yesterday);

        return savedStats;
    }


}

