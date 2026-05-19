package com.mldtech.nilapp.api.contributions.service;

import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.repository.ContributionStatusRepository;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionrepository;
    private final ContributionStatusRepository statusRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;


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

}
