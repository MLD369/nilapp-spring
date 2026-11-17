package com.mldtech.nilapp.api.contributions.controller;

import com.mldtech.nilapp.exceptions.ClosedAllocationException;
import com.mldtech.nilapp.api.contributions.dto.ContributionRequestDTO;
import com.mldtech.nilapp.api.contributions.dto.CreateContributionRequest;
import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.service.ContributionService;
import com.mldtech.nilapp.api.users.dto.ContributionDTO;
import com.mldtech.nilapp.exceptions.ContributionErrorPayload;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contributions")
@RequiredArgsConstructor
public class ContributionController {
    private static final Logger log = LoggerFactory.getLogger(ContributionController.class);
    private final ContributionService contributionService;

    @GetMapping("/user/{userId}/1")
    public List<Contribution> getContributionsForUser(@PathVariable Long userId) {
        return contributionService.getContributionsForUser(userId);
    }
    @GetMapping("/user/{userId}")
    public CustomResponse<List<ContributionDTO>> getContributionsForUserDto(@PathVariable Long userId) {
        return contributionService.getUserContributions(userId);
    }

    @GetMapping("/entity/{entityId}")
    public List<Contribution> getContributionsForEntity(@PathVariable Long entityId) {
        return contributionService.getContributionsForEntity(entityId);
    }

    @GetMapping("/campaign/{campaignId}")
    public List<Contribution> getContributionsForCampaign(@PathVariable Long campaignId) {
        return contributionService.getContributionsForCampaign(campaignId);
    }

    @PostMapping("only")
    public Contribution createContribution(@RequestBody Contribution contribution) {
        return contributionService.createContribution(contribution);
    }

    @PutMapping("/{contributionId}/status/{statusId}")
    public Contribution updateStatus(
            @PathVariable Long contributionId,
            @PathVariable Integer statusId
    ) {
        return contributionService.updateStatus(contributionId, statusId);
    }




//    @PostMapping
//    public Contribution createContribution(@RequestBody CreateContributionRequest request) {
//        return contributionService.createContribution(
//                request.getUserId(),
//                request.getEntityId(),
//                request.getCoins(),
//                request.getSteps(),
//                request.getEntityAllocations(),
//                request.getGroupAllocations(),
//                request.getAdValue(),
//                request.getCoinValue()
//        );
//    }
    @PostMapping("/claim")
    public ResponseEntity<?> claimContribution(@RequestBody ContributionRequestDTO request) {
        System.out.println("claimContribution");
        System.out.println("claimContribution");
        System.out.println(request);
        System.out.println("claimContribution");
        System.out.println("claimContribution");

        Contribution contribution = contributionService.claimContribution(request);
        return ResponseEntity.ok(contribution);

    }
    @PostMapping("/validate")
    public ResponseEntity<ContributionErrorPayload> validateContribution(@RequestBody ContributionRequestDTO dto) {
        ContributionErrorPayload result = contributionService.preflightValidateClaim(dto);
        return ResponseEntity.ok(result);
    }



}
