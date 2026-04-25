package com.mldtech.nilapp.api.contributions.service;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository repository;

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

//    public Contribution updateStatus(Long contributionId, Integer statusId) {
//        Contribution c = repository.findById(contributionId)
//                .orElseThrow(() -> new RuntimeException("Contribution not found"));
//
//        c.set(Long.valueOf(statusId));
//        return repository.save(c);
//    }
}
