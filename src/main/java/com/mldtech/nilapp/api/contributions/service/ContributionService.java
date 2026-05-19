package com.mldtech.nilapp.api.contributions.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.repository.ContributionStatusRepository;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.entitystat.model.EntityStat;
import com.mldtech.nilapp.api.entitystat.repository.EntityStatRepository;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.groupstat.model.GroupStat;
import com.mldtech.nilapp.api.groupstat.repository.GroupStatRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionrepository;
    private final ContributionStatusRepository statusRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;
    private final EntityStatRepository entityStatRepository;
    private final GroupStatRepository groupStatRepository;

    public List<Contribution> getContributionsForUser(Long userId) {
        return contributionrepository.findByUserId(userId);
    }

    public List<Contribution> getContributionsForEntity(Long entityId) {
        return contributionrepository.findByEntityId(entityId);
    }

    public List<Contribution> getContributionsForCampaign(Long campaignId) {
        return contributionrepository.findByCampaignId(campaignId);
    }

    public Contribution createContribution(Contribution contribution) {
        return contributionrepository.save(contribution);
    }

    public Contribution updateStatus(Long contributionId, Integer statusId) {

        Contribution contribution = contributionrepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        ContributionStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        contribution.setContributionStatus(status);

        return contributionrepository.save(contribution);
    }

    public CustomResponse<List<ContributionDTO>> getUserContributions(Long userId) {

        List<Contribution> contributions = contributionrepository.findByUserId(userId);

        if (contributions.isEmpty()) {
            return new CustomResponse<>(
                    "No contributions found for this user",
                    HttpStatus.OK,
                    "200"
            );
        }

        List<ContributionDTO> dtoList = contributions.stream()
                .map(c -> ContributionDTO.builder()
                        .contributionId(c.getContributionId())
                        .entityId(c.getEntityId())
                        .campaignId(c.getCampaignId())
                        .conversionRate(c.getConversionRate()) // TODO might not want to show
                        .amount(c.getCoinsContributed() != null
                                ? c.getCoinsContributed().longValue()
                                : 0L)
                        .stepsContributed(c.getStepsContributed())
                        .coinsContributed(c.getCoinsContributed())
                        .statusCode(c.getContributionStatus().getCode())
                        .statusLabel(c.getContributionStatus().getLabel())
                        .statusDescription(c.getContributionStatus().getDescription())
                        .createdAt(c.getCreatedAt())
                        .build()
                )
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

            var contributions = contributionrepository.findByUserIdAndEntityId(userId, entityId);

            List<ContributionDTO> dtoList = contributions.stream()
                    .map(c -> ContributionDTO.builder()
                            .contributionId(c.getContributionId())
                            .entityId(c.getEntityId())
                            .campaignId(c.getCampaignId())
                            .conversionRate(c.getConversionRate()) // TODO might not want to show
                            .amount(c.getCoinsContributed() != null
                                    ? c.getCoinsContributed().longValue()
                                    : 0L)
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
            var contributions = contributionrepository.findByUserId(userId).stream()
                    .filter(c -> entityIds.contains(c.getEntityId()))
                    .toList();

            List<ContributionDTO> dtoList = contributions.stream()
                    .map(c -> ContributionDTO.builder()
                            .contributionId(c.getContributionId())
                            .entityId(c.getEntityId())
                            .campaignId(c.getCampaignId())
                            .conversionRate(c.getConversionRate()) // TODO might not want to show
                            .amount(c.getCoinsContributed() != null
                                    ? c.getCoinsContributed().longValue()
                                    : 0L)
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
            Map<Long, Integer> entityAllocations,
            Map<Long, Integer> groupAllocations,
            BigDecimal adValue,
            BigDecimal coinValue
    ) {
        // 1) Build allocation snapshot JSON
        ObjectNode root = JsonNodeFactory.instance.objectNode();

        ObjectNode entityNode = root.putObject("entityAllocations");
        entityAllocations.forEach((id, pct) -> entityNode.put(String.valueOf(id), pct));

        ObjectNode groupNode = root.putObject("groupAllocations");
        groupAllocations.forEach((id, pct) -> groupNode.put(String.valueOf(id), pct));

        root.put("adValue", adValue != null ? adValue : BigDecimal.ZERO);
        root.put("coinValue", coinValue != null ? coinValue : BigDecimal.ONE);
        root.put("coinsEarned", coins);
        root.put("steps", steps);

        String snapshotJson = root.toString();

        // 2) Save raw contribution
        Contribution contribution = Contribution.builder()
                .userId(userId)
                .entityId(entityId)
                .coinsContributed(coins)
                .stepsContributed(steps)
                .allocationSnapshot(snapshotJson)
                .build();

        Contribution saved = contributionrepository.save(contribution);

        Long contributionId = saved.getContributionId();

        // 3) Save entity_stats rows
        entityAllocations.forEach((allocEntityId, pct) -> {
            long allocCoins = coins * pct / 100;
            long allocSteps = steps * pct / 100;

            EntityStat es = EntityStat.builder()
                    .contributionId(contributionId)
                    .entityId(allocEntityId)
                    .userId(userId)
                    .coins(allocCoins)
                    .steps(allocSteps)
                    .allocationPercentage(pct)
                    .build();

            entityStatRepository.save(es);
        });

        // 4) Save group_stats rows
        groupAllocations.forEach((groupId, pct) -> {
            long allocCoins = coins * pct / 100;
            long allocSteps = steps * pct / 100;

            GroupStat gs = GroupStat.builder()
                    .contributionId(contributionId)
                    .groupId(groupId)
                    .userId(userId)
                    .coins(allocCoins)
                    .steps(allocSteps)
                    .allocationPercentage(pct)
                    .build();

            groupStatRepository.save(gs);
        });

        return saved;
    }
}
