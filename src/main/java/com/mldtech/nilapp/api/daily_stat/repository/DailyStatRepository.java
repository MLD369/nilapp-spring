package com.mldtech.nilapp.api.daily_stat.repository;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    List<DailyStat> findByUserId(Long userId);

    Optional<DailyStat> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailyStat> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}

