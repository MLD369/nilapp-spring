package com.mldtech.nilapp.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mldtech.nilapp.api.contributions.dto.ContributionRequestDTO;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.users.dto.GoalProgressDTO;
import com.mldtech.nilapp.exceptions.ClosedAllocationException;
import com.mldtech.nilapp.exceptions.ContributionErrorPayload;
import com.mldtech.nilapp.exceptions.InvalidAllocationMathException;
import com.mldtech.nilapp.exceptions.MandatoryEntityMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);

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

    /**
     * Shared validation gate used by both pre-flight validation and final claim submission.
     * Validates JSON structure, entity/group percentage math, goal shares, and open goal status.
     *
     * @param dto the incoming contribution request payload
     * @return the parsed allocation snapshot map if all structural, math, and open-goal checks pass
     */
    public Map<String, Object> validateAllocationSnapshot(ContributionRequestDTO dto, GoalInstanceRepository goalInstanceRepository, ContributionRepository contributionRepository) {
        log.info("Pre-flight guard verification running for incoming contribution path...");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parsedSnapshot = new HashMap<>();

        if (dto.getAllocationSnapshot() != null && !dto.getAllocationSnapshot().isBlank()) {
            try {
                parsedSnapshot = objectMapper.readValue(
                        dto.getAllocationSnapshot(),
                        new TypeReference<Map<String, Object>>() {}
                );
            } catch (Exception e) {
                log.error("Structural error caught parsing snapshot text framework.", e);
                parsedSnapshot = new HashMap<>();
            }
        }

        // ======================================================================
        // 🛡️ CRITICAL STRUCTURAL, STATUS, & MATHEMATICAL GATES
        // ======================================================================
        List<ContributionErrorPayload.ClosedTargetDetail> closedEntities = new ArrayList<>();
        List<ContributionErrorPayload.ClosedTargetDetail> closedGroups = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 🚨 RULE 1: ENFORCE MANDATORY ENTITY ALLOCATIONS ARRAY PRESENCE
        if (!parsedSnapshot.containsKey("entityAllocations") || !(parsedSnapshot.get("entityAllocations") instanceof List)) {
            ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                    .allowedToProcess(false)
                    .statusText("MANDATORY_ENTITY_MISSING")
                    .systemMessage("Please select an entity to support before claiming your points.")
                    .build();

            throw new MandatoryEntityMissingException(
                    dto.getUserId(),
                    "MISSING_ENTITY_ARRAY",
                    "Submitted allocationSnapshot completely lacked the 'entityAllocations' structural key array.",
                    errorDetails
            );
        }

        List<?> entityList = (List<?>) parsedSnapshot.get("entityAllocations");

        // 🚨 RULE 2: REJECT EMPTY ENTITY LIST ARRAYS
        if (entityList.isEmpty()) {
            ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                    .allowedToProcess(false)
                    .statusText("MANDATORY_ENTITY_MISSING")
                    .systemMessage("You must choose at least one entity to support with this claim.")
                    .build();

            throw new MandatoryEntityMissingException(
                    dto.getUserId(),
                    "EMPTY_ENTITY_LIST",
                    "Submitted allocationSnapshot contained an empty 'entityAllocations' array list.",
                    errorDetails
            );
        }

        int totalIncomingSteps = dto.getStepsContributed();
        int totalIncomingCoins = dto.getCoinsContributed();

        double totalEntityPercentageSum = 0.0;
        double totalGroupPercentageSum = 0.0;

        // 🏢 SCAN BLOCK A: PARSE AND VALIDATE DYNAMIC ENTITY SELECTIONS
        for (Object obj : entityList) {
            if (obj instanceof Map<?, ?> entityData) {
                Long entityId = Long.valueOf(entityData.get("entityId").toString());
                double percentage = Double.parseDouble(entityData.get("percentage").toString());
                totalEntityPercentageSum += percentage;

                List<?> goals = (List<?>) entityData.get("goals");
                String dbEntityName = contributionRepository.findEntityNameById(entityId);
                String resolvedEntityName = dbEntityName != null ? dbEntityName : "Entity";

                // 🚨 RULE 3: ENFORCE GOAL ATTACHMENT FOR WEIGHTED POOLS
                if (percentage > 0) {
                    if (goals == null || goals.isEmpty()) {
                        ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                                .allowedToProcess(false)
                                .statusText("ORPHANED_ALLOCATION_WEIGHT")
                                .systemMessage(String.format("You gave points to %s but didn't pick a specific goal. Please select a goal for your points.", resolvedEntityName))
                                .build();

                        throw new MandatoryEntityMissingException(
                                dto.getUserId(),
                                "ORPHANED_ENTITY_WEIGHT",
                                "Entity ID " + entityId + " has a percentage allocation greater than 0 but no goals attached.",
                                errorDetails
                        );
                    }

                    // 📐 MATHEMATICAL COMPLIANCE & INDIVIDUAL GOAL CLOSING CHECK
                    double totalGoalShareSum = 0.0;
                    for (Object goalObj : goals) {
                        if (goalObj instanceof Map<?, ?> goalData) {
                            double share = Double.parseDouble(goalData.get("share").toString());
                            totalGoalShareSum += share;

                            if (share > 0 && goalData.get("goalInstanceId") != null) {
                                Long goalInstanceId = Long.valueOf(goalData.get("goalInstanceId").toString());
                                String goalName = goalData.get("goalName") != null
                                        ? goalData.get("goalName").toString()
                                        : null;

                                boolean isGoalOpen = goalInstanceRepository.isGoalInstanceOpen(goalInstanceId, today);
                                if (!isGoalOpen) {
                                    double effectivePct = (percentage * share) / 100.0;
                                    int stepsAttempted = (int) Math.round(totalIncomingSteps * effectivePct);
                                    int coinsAttempted = (int) Math.round(totalIncomingCoins * effectivePct);
                                    String displayName = goalName != null
                                            ? resolvedEntityName + " - " + goalName
                                            : resolvedEntityName;

                                    closedEntities.add(
                                            ContributionErrorPayload.ClosedTargetDetail.builder()
                                                    .targetId(entityId)
                                                    .name(displayName)
                                                    .stepsAttempted(stepsAttempted)
                                                    .coinsAttempted(coinsAttempted)
                                                    .allocationPercentage(percentage * share)
                                                    .build()
                                    );
                                }
                            }
                        }
                    }

                    if (Math.abs(totalGoalShareSum - 1.0) > 0.0001) {
                        ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                                .allowedToProcess(false)
                                .statusText("INVALID_GOAL_SHARE_SUM")
                                .systemMessage(String.format("The splits you chose for your goals under %s don't add up correctly. Please make sure they equal 100%% of that allocation.", resolvedEntityName))
                                .build();

                        throw new InvalidAllocationMathException(
                                dto.getUserId(),
                                "GOAL_SHARES_ENTITY_" + entityId,
                                totalGoalShareSum,
                                errorDetails
                        );
                    }
                }
            }
        }

        // 🚨 MATHEMATICAL COMPLIANCE: Top-Level Entities Verification Gate
        if (Math.abs(totalEntityPercentageSum - 100.0) > 0.0001) {
            ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                    .allowedToProcess(false)
                    .statusText("INVALID_ENTITY_PERCENTAGE_SUM")
                    .systemMessage("Your entity point split does not equal exactly 100%. Please re-adjust your sliders.")
                    .build();

            throw new InvalidAllocationMathException(
                    dto.getUserId(),
                    "ENTITY_PERCENTAGES",
                    totalEntityPercentageSum,
                    errorDetails
            );
        }

        // 👥 SCAN BLOCK B: PARSE AND VALIDATE DYNAMIC GROUP SELECTIONS (Optional Presence)
        if (parsedSnapshot.containsKey("groupAllocations") && parsedSnapshot.get("groupAllocations") instanceof List<?> groupList) {
            if (!groupList.isEmpty()) {
                for (Object obj : groupList) {
                    if (obj instanceof Map<?, ?> groupData) {
                        Long groupId = Long.valueOf(groupData.get("groupId").toString());
                        double percentage = Double.parseDouble(groupData.get("percentage").toString());
                        totalGroupPercentageSum += percentage;

                        List<?> goals = (List<?>) groupData.get("goals");
                        String dbGroupName = contributionRepository.findGroupNameById(groupId);
                        String resolvedGroupName = dbGroupName != null ? dbGroupName : "Friend Group";

                        // Enforce goals if group weight is active
                        if (percentage > 0) {
                            if (goals == null || goals.isEmpty()) {
                                ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                                        .allowedToProcess(false)
                                        .statusText("ORPHANED_ALLOCATION_WEIGHT")
                                        .systemMessage(String.format("You gave points to %s but didn't pick a specific group goal. Please select a goal.", resolvedGroupName))
                                        .build();
                                throw new MandatoryEntityMissingException(
                                        dto.getUserId(),
                                        "ORPHANED_GROUP_WEIGHT",
                                        "Group ID " + groupId + " has a percentage allocation greater than 0 but no goals attached.",
                                        errorDetails
                                );
                            }
                            // 📐 MATHEMATICAL COMPLIANCE & INDIVIDUAL GROUP GOAL CLOSING CHECK
                            double totalGroupGoalShareSum = 0.0;
                            for (Object goalObj : goals) {
                                if (goalObj instanceof Map goalData) {
                                    double share = Double.parseDouble(goalData.get("share").toString());
                                    totalGroupGoalShareSum += share;
                                    if (share > 0 && goalData.get("goalInstanceId") != null) {
                                        Long goalInstanceId = Long.valueOf(goalData.get("goalInstanceId").toString());
                                        String goalName = goalData.get("goalName") != null
                                                ? goalData.get("goalName").toString()
                                                : null;
                                        boolean isGoalOpen = goalInstanceRepository.isGoalInstanceOpen(goalInstanceId, today);
                                        if (!isGoalOpen) {
                                            double effectivePct = (percentage * share) / 100.0;
                                            int stepsAttempted = (int) Math.round(totalIncomingSteps * effectivePct);
                                            int coinsAttempted = (int) Math.round(totalIncomingCoins * effectivePct);
                                            String displayName = goalName != null
                                                    ? resolvedGroupName + " - " + goalName
                                                    : resolvedGroupName;
                                            closedGroups.add(
                                                    ContributionErrorPayload.ClosedTargetDetail.builder()
                                                            .targetId(groupId)
                                                            .name(displayName)
                                                            .stepsAttempted(stepsAttempted)
                                                            .coinsAttempted(coinsAttempted)
                                                            .allocationPercentage(percentage * share)
                                                            .build()
                                            );
                                        }
                                    }
                                }
                            }
                            if (Math.abs(totalGroupGoalShareSum - 1.0) > 0.0001) {
                                ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                                        .allowedToProcess(false)
                                        .statusText("INVALID_GOAL_SHARE_SUM")
                                        .systemMessage(String.format("The splits you chose for your group goals under %s don't add up correctly. Please make sure they equal 100%% of that allocation.", resolvedGroupName))
                                        .build();
                                throw new InvalidAllocationMathException(
                                        dto.getUserId(),
                                        "GOAL_SHARES_GROUP_" + groupId,
                                        totalGroupGoalShareSum,
                                        errorDetails
                                );
                            }
                        }
                    }
                }
// 🚨 MATHEMATICAL COMPLIANCE: Top-Level Groups Verification Gate
                if (Math.abs(totalGroupPercentageSum - 100.0) > 0.0001) {
                    ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                            .allowedToProcess(false)
                            .statusText("INVALID_GROUP_PERCENTAGE_SUM")
                            .systemMessage("Your group point split does not equal exactly 100%. Please re-adjust your sliders.")
                            .build();
                    throw new InvalidAllocationMathException(
                            dto.getUserId(),
                            "GROUP_PERCENTAGES",
                            totalGroupPercentageSum,
                            errorDetails
                    );
                }
            }
        }
// 🛑 CLOSING CHECK: REJECT TRANSACTION IMMEDIATELY IF TARGETS ARE CLOSED
        if (!closedEntities.isEmpty() || !closedGroups.isEmpty()) {
            int totalClosed = closedEntities.size() + closedGroups.size();
            ContributionErrorPayload errorDetails = ContributionErrorPayload.builder()
                    .allowedToProcess(false)
                    .statusText("CLOSED_TARGETS_FOUND")
                    .systemMessage("One or more of the goals you selected have just finished! Please review your dashboard and choose an active goal.")
                    .closedEntities(closedEntities.isEmpty() ? null : closedEntities)
                    .closedGroups(closedGroups.isEmpty() ? null : closedGroups)
                    .build();
            throw new ClosedAllocationException(dto.getUserId(), totalClosed, errorDetails);
        }
        return parsedSnapshot;
    }



}
