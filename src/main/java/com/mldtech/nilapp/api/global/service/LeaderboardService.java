package com.mldtech.nilapp.api.global.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.daily_stat.repository.DailyStatRepository;
import com.mldtech.nilapp.api.users.children.UserStreak.repository.UserStreakRepository;
import com.mldtech.nilapp.api.users.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final ContributionRepository contributionRepository;
    private final DailyStatRepository dailyStatRepository;
    private final UserStreakRepository userStreakRepository;

    public List<StreakLeaderDTO> getTopStreaks() {
        List<StreakLeaderDTO> list = userStreakRepository.getTopStreaks()
                .stream()
                .map(r -> StreakLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .currentStreak(((Number) r[2]).intValue())
                        .build())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<StepLeaderDTO> getTopSteps(LocalDate start, LocalDate end) {
        List<StepLeaderDTO> list = dailyStatRepository.getTopSteps(start, end)
                .stream()
                .map(r -> StepLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .totalSteps(((Number) r[2]).longValue())
                        .build())
                .toList();
        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<CoinLeaderDTO> getTopCoins(LocalDateTime start, LocalDateTime end) {
        List<CoinLeaderDTO> list =  contributionRepository.getTopCoins(start, end)
                .stream()
                .map(r -> CoinLeaderDTO.builder()
                        .userId(((Number) r[0]).longValue())
                        .username((String) r[1])
                        .coins(((Number) r[2]).longValue())
                        .build())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }

    public List<EntityNilLeaderDTO> getEntityNilLeaders(LocalDateTime start, LocalDateTime end) {
        List<EntityNilLeaderDTO> list = contributionRepository.getEntityNilLeaderboard(start, end);

        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));

        return list;
    }




    public GlobalLeaderboardResponse getLeaderboard(String startDate, String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startTs = start.atStartOfDay();
        LocalDateTime endTs = end.atTime(23, 59, 59);

        return GlobalLeaderboardResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .stepLeaders(getTopSteps(start, end))
                .coinLeaders(getTopCoins(startTs, endTs))
                .streakLeaders(getTopStreaks())
                .entityNilLeaders(getEntityNilLeaders(startTs, endTs))
                .build();
    }
}
