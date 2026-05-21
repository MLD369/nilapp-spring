package com.mldtech.nilapp.api.contributions.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.repository.ContributionStatusRepository;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.dto.ContributionPeriodStatsDTO;
import com.mldtech.nilapp.api.users.dto.EntityAllocationDTO;
import com.mldtech.nilapp.api.users.dto.GroupAllocationDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import com.mldtech.nilapp.mapper.ContributionMapper;
import com.mldtech.nilapp.mapper.ContributionStatusMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final ContributionStatusRepository statusRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;

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


}
