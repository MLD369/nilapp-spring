//package com.mldtech.nilapp.api.sponsors.children.SponserCampaign.service;
//
//import com.mldtech.nilapp.api.sponsors.children.SponserCampaign.model.SponsorCampaign;
//import com.mldtech.nilapp.api.sponsors.children.SponserCampaign.repository.SponsorCampaignRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SponsorCampaignService {
//
//    private final SponsorCampaignRepository repository;
//
//    public List<SponsorCampaign> getCampaignsForSponsor(Long sponsorId) {
//        return repository.findBySponsorId(sponsorId);
//    }
//
//    public List<SponsorCampaign> getCampaignsForEntity(Long entityId) {
//        return repository.findByEntityId(entityId);
//    }
//
//    public SponsorCampaign getCampaign(Long campaignId) {
//        return repository.findById(campaignId)
//                .orElseThrow(() -> new RuntimeException("Campaign not found"));
//    }
//
//    public SponsorCampaign createCampaign(SponsorCampaign campaign) {
//        return repository.save(campaign);
//    }
//
//    public SponsorCampaign updateCampaign(Long campaignId, SponsorCampaign updated) {
//        SponsorCampaign existing = getCampaign(campaignId);
//
//        existing.setSponsorId(updated.getSponsorId());
//        existing.setEntityId(updated.getEntityId());
//        existing.setPayoutPerStep(updated.getPayoutPerStep());
//        existing.setPayoutPerCoin(updated.getPayoutPerCoin());
//        existing.setStartDate(updated.getStartDate());
//        existing.setEndDate(updated.getEndDate());
//
//        return repository.save(existing);
//    }
//
//    public void deleteCampaign(Long campaignId) {
//        repository.deleteById(campaignId);
//    }
//}
//
