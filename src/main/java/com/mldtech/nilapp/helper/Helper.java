package com.mldtech.nilapp.helper;

import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.users.dto.GoalProgressDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;

public class Helper {
    public String getPeriodKey(LocalDateTime date, String periodType) {
        LocalDate d = date.toLocalDate();

        return switch (periodType.toUpperCase()) {
            case "DAILY" -> d.toString(); // 2026-05-24
            case "WEEKLY" -> d.getYear() + "-W" + d.get(WeekFields.ISO.weekOfWeekBasedYear());
            case "MONTHLY" -> d.getYear() + "-" + String.format("%02d", d.getMonthValue());
            case "YEARLY" -> String.valueOf(d.getYear());
            default -> d.toString();
        };
    }
    public GoalProgressDTO toGoalProgressDTO(GoalInstance gi) {
        if (gi == null) {
            return null;
        }

        var goal = gi.getGoal();
        if (goal == null) {
            return null;
        }

        var achievement = goal.getAchievement();

        Long requiredSteps = (achievement != null) ? achievement.getRequiredSteps() : null;
        Long requiredCoins = (achievement != null) ? achievement.getRequiredCoins() : null;

        Long stepsContributed = gi.getStepsContributed();
        Long coinsContributed = gi.getCoinsContributed();

        Long stepsNeeded = gi.getStepsNeeded();
        Long coinsNeeded = gi.getCoinsNeeded();

        Long currentAmount = 0L;
        double progressPct = 0.0;

        if (requiredSteps != null && requiredSteps > 0) {
            long contributed = stepsContributed != null ? stepsContributed : 0L;
            currentAmount = Math.max(0L, contributed);
            progressPct = (currentAmount * 100.0) / requiredSteps;
        } else if (requiredCoins != null && requiredCoins > 0) {
            long contributed = coinsContributed != null ? coinsContributed : 0L;
            currentAmount = Math.max(0L, contributed);
            progressPct = (currentAmount * 100.0) / requiredCoins;
        } else {
            // Fallback if neither target is set: default to steps if available, otherwise coins
            long fallback = stepsContributed != null ? stepsContributed : (coinsContributed != null ? coinsContributed : 0L);
            currentAmount = Math.max(0L, fallback);
        }

        boolean isExpiredByDate = gi.getEndDate() != null && gi.getEndDate().isBefore(LocalDate.now());
        boolean isComplete = Boolean.TRUE.equals(gi.getIsComplete()) || progressPct >= 100.0;
        boolean isActive = Boolean.TRUE.equals(gi.getIsActive()) && !isComplete && !isExpiredByDate;

        String status;
        if (isComplete) {
            status = "COMPLETED";
        } else if (isExpiredByDate) {
            status = "EXPIRED";
        } else if (isActive) {
            status = "ACTIVE";
        } else {
            status = "INACTIVE";
        }

        return GoalProgressDTO.builder()
                .goalId(goal.getGoalId())
                .goalName(goal.getGoal())
                .requiredSteps(requiredSteps)
                .requiredCoins(requiredCoins)
                .currentAmount(currentAmount)
                .progressPct(progressPct)
                .stepsContributed(stepsContributed != null ? stepsContributed : 0L)
                .coinsContributed(coinsContributed != null ? coinsContributed : 0L)
                .stepsNeeded(stepsNeeded != null ? stepsNeeded : (requiredSteps != null ? requiredSteps : 0L))
                .coinsNeeded(coinsNeeded != null ? coinsNeeded : (requiredCoins != null ? requiredCoins : 0L))
                .dollarAmount(gi.getDollarAmount() != null ? gi.getDollarAmount() : BigDecimal.ZERO)
                .goalInstanceId(gi.getGoalInstanceId())
                .active(isActive)
                .isComplete(isComplete)
                .status(status)
                .joined(true)
                .build();
    }



    public GoalProgressDTO mergeGoalProgress(List<GoalProgressDTO> goals) {

        if (goals == null || goals.isEmpty()) {
            return null;
        }

        GoalProgressDTO first = goals.get(0);

        long totalSteps = goals.stream()
                .mapToLong(g -> g.getStepsContributed() != null ? g.getStepsContributed() : 0L)
                .sum();

        long totalCoins = goals.stream()
                .mapToLong(g -> g.getCoinsContributed() != null ? g.getCoinsContributed() : 0L)
                .sum();

        BigDecimal totalDollarAmount = goals.stream()
                .map(g -> g.getDollarAmount() != null ? g.getDollarAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long requiredSteps = first.getRequiredSteps();
        Long requiredCoins = first.getRequiredCoins();

        Long currentAmount = requiredSteps != null
                ? totalSteps
                : totalCoins;

        double progressPct = 0.0;

        if (requiredSteps != null && requiredSteps > 0) {
            progressPct = (totalSteps * 100.0) / requiredSteps;
        } else if (requiredCoins != null && requiredCoins > 0) {
            progressPct = (totalCoins * 100.0) / requiredCoins;
        }

        return GoalProgressDTO.builder()
                .goalId(first.getGoalId())
                .goalName(first.getGoalName())

                .requiredSteps(requiredSteps)
                .requiredCoins(requiredCoins)

                .currentAmount(currentAmount)
                .progressPct(progressPct)

                .stepsContributed(totalSteps)
                .coinsContributed(totalCoins)

                .stepsNeeded(requiredSteps)
                .coinsNeeded(requiredCoins)

                .dollarAmount(totalDollarAmount)

                .isComplete(progressPct >= 100.0)
                .build();
    }




}
