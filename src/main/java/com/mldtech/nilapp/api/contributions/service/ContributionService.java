package com.mldtech.nilapp.api.contributions.service;

import com.mldtech.nilapp.api.contributions.children.ContributionStatus.model.ContributionStatus;
import com.mldtech.nilapp.api.contributions.children.ContributionStatus.repository.ContributionStatusRepository;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
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

    private final ContributionRepository repository;
    private final ContributionStatusRepository statusRepository;

    public List<Contribution> getContributionsForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Contribution> getContributionsForEntity(Long entityId) {
        return repository.findByEntityId(entityId);
    }

    public List<Contribution> getContributionsForCampaign(Long campaignId) {
        return repository.findByCampaignId(campaignId);
    }

    public Contribution createContribution(Contribution contribution) {
        return repository.save(contribution);
    }

    public Contribution updateStatus(Long contributionId, Integer statusId) {

        Contribution contribution = repository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        ContributionStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        contribution.setContributionStatus(status);

        return repository.save(contribution);
    }

    public CustomResponse<List<ContributionDTO>> getUserContributions(Long userId) {

        List<Contribution> contributions = repository.findByUserId(userId);

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


}
