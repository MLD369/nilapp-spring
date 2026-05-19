package com.mldtech.nilapp.api.contributions.controller;

import com.mldtech.nilapp.api.contributions.dto.ContributionSummaryDTO;
import com.mldtech.nilapp.api.contributions.dto.CreateContributionRequest;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.service.ContributionService;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contributions")
@RequiredArgsConstructor
public class ContributionController {

    private final ContributionService service;

    @GetMapping("/user/{userId}/1")
    public List<Contribution> getContributionsForUser(@PathVariable Long userId) {
        return service.getContributionsForUser(userId);
    }
    @GetMapping("/user/{userId}")
    public CustomResponse<List<ContributionDTO>> getContributionsForUserDto(@PathVariable Long userId) {
        return service.getUserContributions(userId);
    }

    @GetMapping("/entity/{entityId}")
    public List<Contribution> getContributionsForEntity(@PathVariable Long entityId) {
        return service.getContributionsForEntity(entityId);
    }

    @GetMapping("/campaign/{campaignId}")
    public List<Contribution> getContributionsForCampaign(@PathVariable Long campaignId) {
        return service.getContributionsForCampaign(campaignId);
    }

    @PostMapping("only")
    public Contribution createContribution(@RequestBody Contribution contribution) {
        return service.createContribution(contribution);
    }

    @PutMapping("/{contributionId}/status/{statusId}")
    public Contribution updateStatus(
            @PathVariable Long contributionId,
            @PathVariable Integer statusId
    ) {
        return service.updateStatus(contributionId, statusId);
    }


        private final ContributionService contributionService;

    @PostMapping
    public Contribution createContribution(@RequestBody CreateContributionRequest request) {
        return contributionService.createContribution(
                request.getUserId(),
                request.getEntityId(),
                request.getCoins(),
                request.getSteps(),
                request.getEntityAllocations(),
                request.getGroupAllocations(),
                request.getAdValue(),
                request.getCoinValue()
        );
    }


}
