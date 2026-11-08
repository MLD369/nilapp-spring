package com.mldtech.nilapp.api.contributions.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.repository.ContributionStatusRepository;
import com.mldtech.nilapp.api.contributions.children.ContributionType.model.ContributionType;
import com.mldtech.nilapp.api.contributions.dto.ContributionRequestDTO;
import com.mldtech.nilapp.api.contributions.dto.ContributionTotalDTO;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.dto.ContributionPeriodStatsDTO;
import com.mldtech.nilapp.api.users.dto.EntityAllocationDTO;
import com.mldtech.nilapp.api.users.dto.GroupAllocationDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.exceptions.ClosedAllocationException;
import com.mldtech.nilapp.exceptions.InvalidAllocationMathException;
import com.mldtech.nilapp.exceptions.MandatoryEntityMissingException;
import com.mldtech.nilapp.helper.CustomResponse;
import com.mldtech.nilapp.mapper.ContributionMapper;
import com.mldtech.nilapp.mapper.ContributionStatusMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final ContributionStatusRepository statusRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;


    // Define your default IDs as constants
    private static final Integer DEFAULT_STATUS_ID = 1; // e.g., PENDING
    private static final Integer DEFAULT_TYPE_ID = 1;   // e.g., Advertisement

    public ContributionTotalDTO getUserMonthlyTotals(Long userId,
                                                           LocalDateTime start,
                                                           LocalDateTime end) {
        return contributionRepository.getUserMonthlyTotals(userId,start,end);
    }
    public List<Contribution> getContributionsForUser(Long userId) {
        return contributionRepository.findByUserId(userId);
    }

    public List<Contribution> getContributionsForEntity(Long entityId) {
        return contributionRepository.findByEntityId(entityId);
    }

    public List<Contribution> getContributionsForCampaign(Long campaignId) {
        return contributionRepository.findByCampaignId(campaignId);
    }

    public Contribution createContribution(Contribution contribution) {
        return contributionRepository.save(contribution);
    }

    public Contribution updateStatus(Long contributionId, Integer statusId) {

        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        ContributionStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        contribution.setContributionStatus(status);

        return contributionRepository.save(contribution);
    }

    public CustomResponse<List<ContributionDTO>> getUserContributions(Long userId) {

        List<Contribution> contributions = contributionRepository.findByUserId(userId);

        if (contributions.isEmpty()) {
            return new CustomResponse<>(
                    "No contributions found for this user",
                    HttpStatus.OK,
                    "200"
            );
        }

        List<ContributionDTO> dtoList = contributions.stream()
                .map(ContributionMapper::toDTO)

                .toList();



        return new CustomResponse<>(
                dtoList,
                HttpStatus.OK,
                "200"
        );
    }


    public CustomResponse<List<ContributionDTO>> getUserContributionsByFilter(
            Long userId,
            Long entityId,
            Long groupId
    ) {

        // Validate user
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new CustomResponse<>(
                    "User not found",
                    HttpStatus.BAD_REQUEST,
                    "400"
            );
        }

        // ENTITY FILTER
        if (entityId != null) {

            var contributions = contributionRepository.findByUserIdAndEntityId(userId, entityId);

            List<ContributionDTO> dtoList = contributions.stream()
                    .map(c -> ContributionDTO.builder()
                            .contributionId(c.getContributionId())
                            .campaignId(c.getCampaignId())
                            .conversionRate(c.getConversionRate()) // TODO might not want to show
                            .amount(c.getCoinsContributed() != null
                                    ? c.getCoinsContributed()
                                    : 0)
                            .stepsContributed(c.getStepsContributed())
                            .coinsContributed(c.getCoinsContributed())
                            .statusCode(c.getContributionStatus().getCode())
                            .statusLabel(c.getContributionStatus().getLabel())
                            .statusDescription(c.getContributionStatus().getDescription())
                            .createdAt(c.getCreatedAt())
                            .build()
                    )
                    .toList();

            return new CustomResponse<>(dtoList, HttpStatus.OK, "200");
        }

        // GROUP FILTER
        if (groupId != null) {

            var group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                return new CustomResponse<>(
                        "Group not found",
                        HttpStatus.BAD_REQUEST,
                        "400"
                );
            }

            // Get all entities in this group (ManyToMany)
            var groupEntities = group.getEntities(); // List<Entities>

            var entityIds = groupEntities.stream()
                    .map(e -> e.getEntityId())
                    .toList();

            // Get all contributions for this user
            var contributions = contributionRepository.findByUserId(userId).stream()
                    .filter(c -> entityIds.contains(c.getEntityId()))
                    .toList();

            List<ContributionDTO> dtoList = contributions.stream()
                    .map(c -> ContributionDTO.builder()
                            .contributionId(c.getContributionId())
                            .campaignId(c.getCampaignId())
                            .conversionRate(c.getConversionRate()) // TODO might not want to show
                            .amount(c.getCoinsContributed() != null
                                    ? c.getCoinsContributed()
                                    : 0)
                            .stepsContributed(c.getStepsContributed())
                            .coinsContributed(c.getCoinsContributed())
                            .statusCode(c.getContributionStatus().getCode())
                            .statusLabel(c.getContributionStatus().getLabel())
                            .statusDescription(c.getContributionStatus().getDescription())
                            .createdAt(c.getCreatedAt())
                            .build()
                    )
                    .toList();

            return new CustomResponse<>(dtoList, HttpStatus.OK, "200");
        }


        return new CustomResponse<>(
                "Must provide either entityId or groupId",
                HttpStatus.BAD_REQUEST,
                "400"
        );
    }

    // TODO not used delete?
    @Transactional
    public Contribution createContribution(
            Long userId,
            Long entityId,
            int coins,
            int steps,
            List<EntityAllocationDTO> entityAllocations,
            List<GroupAllocationDTO> groupAllocations,
            BigDecimal adValue,
            BigDecimal coinValue
    ) {

        // 1) Build JSONB map
        Map<String, Object> snapshot = new HashMap<>();

        snapshot.put("entityAllocations",
                entityAllocations.stream()
                        .map(e -> Map.of(
                                "entityId", e.getEntityId(),
                                "percentage", e.getPercentage()
                        ))
                        .toList()
        );

        snapshot.put("groupAllocations",
                groupAllocations.stream()
                        .map(g -> Map.of(
                                "groupId", g.getGroupId(),
                                "percentage", g.getPercentage()
                        ))
                        .toList()
        );

        // 2) Build Contribution entity
        Contribution contribution = Contribution.builder()
                .userId(userId)
                .campaignId(entityId) // or your real campaignId
                .stepsContributed(steps)
                .coinsContributed(coins)
                .conversionRate(coinValue)
                .allocationSnapshot(snapshot)   // <-- JSONB MAP, not string
                .build();

        return contributionRepository.save(contribution);
    }
    public CustomResponse<List<ContributionPeriodStatsDTO>> getContributionStats(
            Long userId,
            Long entityId,
            Long groupId,
            String period,        // "MONTH" or "YEAR"
            String status,        // PENDING, VERIFIED, PAID, REJECTED
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        ContributionStatusMapper csm = new ContributionStatusMapper();
        Long statusId = csm.mapStatusToId(status);

        boolean isMonth = period.equalsIgnoreCase("MONTH");

        List<Object[]> rows;

        if (entityId != null) {
            rows = contributionRepository.getEntityStats(
                    userId,
                    entityId,
                    statusId,
                    startDate,
                    endDate
            );
        } else {
            rows = contributionRepository.getGroupStats(
                    userId,
                    groupId,
                    statusId,
                    startDate,
                    endDate
            );
        }

        List<ContributionPeriodStatsDTO> dtoList = rows.stream()
                .map(r -> ContributionPeriodStatsDTO.builder()
                        .periodLabel(isMonth ? (String) r[0] : (String) r[1]) // month_label or year_label
                        .entityId(entityId)
                        .groupId(groupId)
                        .totalSteps(((BigDecimal) r[3]).intValue())
                        .totalCoins(((BigDecimal) r[4]).intValue())
                        .avgSteps(((BigDecimal) r[3]).doubleValue())
                        .avgCoins(((BigDecimal) r[4]).doubleValue())
                        .build()
                ).toList();

        System.out.println(dtoList);

        return new CustomResponse<>(dtoList, HttpStatus.OK, "200");
    }
    /**
     * Ingests, validates, and persists user ad workout claims.
     * Enforces strict mathematical limits ensuring that total allocations and sub-goal shares
     * equal exactly 100% of the submission weight.
     *
     * @param dto the raw payload metadata received from the client application
     * @return the completely processed and saved Contribution entity record
     * @throws MandatoryEntityMissingException if no corporate entity is assigned to the claim
     * @throws ClosedAllocationException if an assignment points to a finished campaign target
     * @throws InvalidAllocationMathException if total weight matrices fail to equal exactly 100%
     */
    @Transactional
    public Contribution claimContribution(ContributionRequestDTO dto) {
        log.info("Pre-flight guard verification running for incoming contribution claim path...");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parsedSnapshot = new HashMap<>();

        if (dto.getAllocationSnapshot() != null && !dto.getAllocationSnapshot().isBlank()) {
            try {
                parsedSnapshot = objectMapper.readValue(
                        dto.getAllocationSnapshot(),
                        new TypeReference<Map<String, Object>>() {
                        }
                );
            } catch (Exception e) {
                log.error("Structural error caught parsing snapshot text framework.", e);
                parsedSnapshot = new HashMap<>();
            }
        } else {
            parsedSnapshot = new HashMap<>();
        }

        // ======================================================================
        // 🛡️ CRITICAL STRUCTURAL, STATUS, & MATHEMATICAL GATES
        // ======================================================================
        List<Map<String, Object>> closedEntities = new ArrayList<>();
        List<Map<String, Object>> closedGroups = new ArrayList<>();

        // 🚨 RULE 1: ENFORCE MANDATORY ENTITY ALLOCATIONS ARRAY PRESENCE
        if (!parsedSnapshot.containsKey("entityAllocations") || !(parsedSnapshot.get("entityAllocations") instanceof List)) {
            Map<String, Object> errorDetails = Map.of(
                    "allowedToProcess", false,
                    "statusText", "MANDATORY_ENTITY_MISSING",
                    "systemMessage", "Please select a entity to support before claiming your points."
            );
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
            Map<String, Object> errorDetails = Map.of(
                    "allowedToProcess", false,
                    "statusText", "MANDATORY_ENTITY_MISSING",
                    "systemMessage", "You must choose at least one entity to support with this claim."
            );
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
            if (obj instanceof Map) {
                Map<?, ?> entityData = (Map<?, ?>) obj;
                Long entityId = Long.valueOf(entityData.get("entityId").toString());
                double percentage = Double.parseDouble(entityData.get("percentage").toString());
                totalEntityPercentageSum += percentage;

                List<?> goals = (List<?>) entityData.get("goals");
                String dbEntityName = contributionRepository.findEntityNameById(entityId);
                String resolvedEntityName = dbEntityName != null ? dbEntityName : "Entity";


                // 🚨 RULE 3: ENFORCE GOAL ATTACHMENT FOR WEIGHTED POOLS
                if (percentage > 0) {
                    if (goals == null || goals.isEmpty()) {
                        Map<String, Object> errorDetails = Map.of(
                                "allowedToProcess", false,
                                "statusText", "ORPHANED_ALLOCATION_WEIGHT",
                                "systemMessage", String.format("You gave points to %s but didn't pick a specific goal. Please select a goal for your points.", resolvedEntityName)
                        );
                        throw new MandatoryEntityMissingException(
                                dto.getUserId(),
                                "ORPHANED_ENTITY_WEIGHT",
                                "Entity ID " + entityId + " has a percentage allocation greater than 0 but no goals attached.",
                                errorDetails
                        );
                    }

                    // 📐 MATHEMATICAL COMPLIANCE: Verify inner goal shares sum up to exactly 1.0 (100%)
                    double totalGoalShareSum = 0.0;
                    for (Object goalObj : goals) {
                        if (goalObj instanceof Map) {
                            Map<?, ?> goalData = (Map<?, ?>) goalObj;
                            totalGoalShareSum += Double.parseDouble(goalData.get("share").toString());
                        }
                    }
                    if (Math.abs(totalGoalShareSum - 1.0) > 0.0001) {
                        Map<String, Object> errorDetails = Map.of(
                                "allowedToProcess", false,
                                "statusText", "INVALID_GOAL_SHARE_SUM",
                                "systemMessage", String.format("The splits you chose for your goals under %s don't add up correctly. Please make sure they equal 100%% of that allocation.", resolvedEntityName)
                        );
                        throw new InvalidAllocationMathException(dto.getUserId(), "GOAL_SHARES_ENTITY_" + entityId, totalGoalShareSum, errorDetails);
                    }
                }

                // Check active timeline instance status flags in DB
                boolean isEntityOpen = contributionRepository.isEntityGoalActive(entityId);
                if (!isEntityOpen) {
                    int stepsAttempted = (int) Math.round((totalIncomingSteps * percentage) / 100.0);
                    int coinsAttempted = (int) Math.round((totalIncomingCoins * percentage) / 100.0);

                    closedEntities.add(Map.of(
                            "targetId", entityId,
                            "name", resolvedEntityName,
                            "stepsAttempted", stepsAttempted,
                            "coinsAttempted", coinsAttempted,
                            "allocationPercentage", percentage
                    ));
                }
            }
        }

        // 🚨 MATHEMATICAL COMPLIANCE: Top-Level Entities Verification Gate
        if (Math.abs(totalEntityPercentageSum - 100.0) > 0.0001) {
            Map<String, Object> errorDetails = Map.of(
                    "allowedToProcess", false,
                    "statusText", "INVALID_ENTITY_PERCENTAGE_SUM",
                    "systemMessage", "Your entity point split does not equal exactly 100%. Please re-adjust your sliders."
            );
            throw new InvalidAllocationMathException(dto.getUserId(), "ENTITY_PERCENTAGES", totalEntityPercentageSum, errorDetails);
        }

        // 👥 SCAN BLOCK B: PARSE AND VALIDATE DYNAMIC GROUP SELECTIONS (Optional Presence)
        if (parsedSnapshot.containsKey("groupAllocations") && parsedSnapshot.get("groupAllocations") instanceof List) {
            List<?> groupList = (List<?>) parsedSnapshot.get("groupAllocations");

            if (!groupList.isEmpty()) {
                for (Object obj : groupList) {
                    if (obj instanceof Map) {
                        Map<?, ?> groupData = (Map<?, ?>) obj;
                        Long groupId = Long.valueOf(groupData.get("groupId").toString());
                        double percentage = Double.parseDouble(groupData.get("percentage").toString());
                        totalGroupPercentageSum += percentage;

                        List<?> goals = (List<?>) groupData.get("goals");
                        String dbGroupName = contributionRepository.findGroupNameById(groupId);
                        String resolvedGroupName = dbGroupName != null ? dbGroupName : "Friend Group";
                        // Enforce goals if group weight is active
                        if (percentage > 0) {
                            if (goals == null || goals.isEmpty()) {
                                Map<String, Object> errorDetails = Map.of(
                                        "allowedToProcess", false,
                                        "statusText", "ORPHANED_ALLOCATION_WEIGHT",
                                        "systemMessage", String.format("You gave points to %s but didn't pick a specific group goal. Please select a goal.", resolvedGroupName)
                                );
                                throw new MandatoryEntityMissingException(
                                        dto.getUserId(),
                                        "ORPHANED_GROUP_WEIGHT",
                                        "Group ID " + groupId + " has a percentage allocation greater than 0 but no goals attached.",
                                        errorDetails
                                );
                            }
                            // 📐 MATHEMATICAL COMPLIANCE: Verify inner group goal shares sum up to exactly 1.0 (100%)
                            double totalGroupGoalShareSum = 0.0;
                            for (Object goalObj : goals) {
                                if (goalObj instanceof Map) {
                                    Map goalData = (Map) goalObj;
                                    totalGroupGoalShareSum += Double.parseDouble(goalData.get("share").toString());
                                }
                            }
                            if (Math.abs(totalGroupGoalShareSum - 1.0) > 0.0001) {
                                Map<String, Object> errorDetails = Map.of(
                                        "allowedToProcess", false,
                                        "statusText", "INVALID_GOAL_SHARE_SUM",
                                        "systemMessage", String.format("The splits you chose for your group goals under %s don't add up correctly. Please make sure they equal 100%% of that allocation.", resolvedGroupName)
                                );
                                throw new InvalidAllocationMathException(dto.getUserId(), "GOAL_SHARES_GROUP_" + groupId, totalGroupGoalShareSum, errorDetails);
                            }
                        }
                        boolean isGroupOpen = contributionRepository.isGroupGoalActive(groupId);
                        if (!isGroupOpen) {
                            int stepsAttempted = (int) Math.round((totalIncomingSteps * percentage) / 100.0);
                            int coinsAttempted = (int) Math.round((totalIncomingCoins * percentage) / 100.0);
                            closedGroups.add(Map.of(
                                    "targetId", groupId,
                                    "name", resolvedGroupName,
                                    "stepsAttempted", stepsAttempted,
                                    "coinsAttempted", coinsAttempted,
                                    "allocationPercentage", percentage
                            ));
                        }
                    }
                }
                // 🚨 MATHEMATICAL COMPLIANCE: Top-Level Groups Verification Gate (Only triggers if groups are used)
                if (Math.abs(totalGroupPercentageSum - 100.0) > 0.0001) {
                    Map<String, Object> errorDetails = Map.of(
                            "allowedToProcess", false,
                            "statusText", "INVALID_GROUP_PERCENTAGE_SUM",
                            "systemMessage", "Your group point split does not equal exactly 100%. Please re-adjust your sliders."
                    );
                    throw new InvalidAllocationMathException(dto.getUserId(), "GROUP_PERCENTAGES", totalGroupPercentageSum, errorDetails);
                }
            }
        }
        // 🛑 CLOSING CHECK: REJECT TRANSACTION IMMEDIATELY IF TARGETS ARE CLOSED
        if (!closedEntities.isEmpty() || !closedGroups.isEmpty()) {
            int totalClosed = closedEntities.size() + closedGroups.size();
            Map<String, Object> errorDetails = Map.of(
                    "allowedToProcess", false,
                    "statusText", "CLOSED_TARGETS_FOUND",
                    "systemMessage", "One or more of the goals you selected have just finished! Please review your dashboard and choose an active goal.",
                    "closedEntities", closedEntities,
                    "closedGroups", closedGroups
            );
            throw new ClosedAllocationException(dto.getUserId(), totalClosed, errorDetails);
        }
        // ======================================================================
        // RUN ORIGINAL PROCESSING IF THE TARGETS ARE VALID & MATHEMATICALLY BALANCED
        // ======================================================================
        Integer statusId = (dto.getContributionStatusId() != null && dto.getContributionStatusId() > 0)
                ? dto.getContributionStatusId()
                : DEFAULT_STATUS_ID;
        Integer typeId = (dto.getContributionTypeId() != null && dto.getContributionTypeId() > 0)
                ? dto.getContributionTypeId()
                : DEFAULT_TYPE_ID;

        ContributionStatus statusStub = new ContributionStatus();
        statusStub.setContributionStatusId(statusId);
        ContributionType typeStub = new ContributionType();
        typeStub.setContributionTypeId(typeId);
        Contribution contribution = Contribution.builder()
                .userId(dto.getUserId())
                .entityId(dto.getEntityId())
                .campaignId(dto.getCampaignId())
                .stepsContributed(dto.getStepsContributed())
                .coinsContributed(dto.getCoinsContributed())
                .conversionRate(dto.getConversionRate())
                .adSessionId(dto.getAdSessionId())
                .payoutBatchId(dto.getPayoutBatchId())
                .contributionStatus(statusStub)
                .contributionType(typeStub)
                .allocationSnapshot(parsedSnapshot)
                .build();
        log.info("Validation cleared. Committing new transaction record to master database ledger row.");
        return contributionRepository.save(contribution);
    }

}
