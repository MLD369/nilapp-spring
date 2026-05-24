package com.mldtech.nilapp.api.daily_stat.repository;

import com.mldtech.nilapp.api.daily_stat.model.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    List<DailyStat> findByUserId(Long userId);

    Optional<DailyStat> findByUserIdAndDate(Long userId, LocalDate date);

    List<DailyStat> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

    @Query("""
    SELECT u.userId, u.username, SUM(d.totalSteps)
    FROM DailyStat d
    JOIN User u ON u.userId = d.userId
    WHERE d.date BETWEEN :start AND :end
    GROUP BY u.userId, u.username
    ORDER BY SUM(d.totalSteps) DESC
""")
    List<Object[]> getTopSteps(LocalDate start, LocalDate end);




}

