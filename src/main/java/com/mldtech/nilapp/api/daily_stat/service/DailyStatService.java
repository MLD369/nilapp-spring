package com.mldtech.nilapp.api.daily_stat.service;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyStatService {

    private final DailyStatRepository repository;

    public List<DailyStat> getStatsForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public DailyStat getStatForDay(Long userId, LocalDate date) {
        return repository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new RuntimeException("Daily stat not found"));
    }

    public List<DailyStat> getStatsForRange(Long userId, LocalDate start, LocalDate end) {
        return repository.findByUserIdAndDateBetween(userId, start, end);
    }

    public DailyStat createOrUpdateDailyStat(DailyStat stat) {

        // If exists, update instead of creating a duplicate
        return repository.findByUserIdAndDate(stat.getUserId(), stat.getDate())
                .map(existing -> {
                    existing.setTotalSteps(stat.getTotalSteps());
                    existing.setCoins(stat.getCoins());
                    existing.setDistanceMeters(stat.getDistanceMeters());
                    existing.setDeviceSource(stat.getDeviceSource());
                    existing.setSyncTimestamp(LocalDateTime.now());
                    existing.setCalories(stat.getCalories());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    stat.setSyncTimestamp(LocalDateTime.now());
                    return repository.save(stat);
                });
    }
}

