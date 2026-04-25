//package com.mldtech.nilapp.api.sponsors.children.SponserCampaign.controller;
//
//import com.mldtech.nilapp.api.sponsors.children.SponserCampaign.model.SponsorCampaign;
//import com.mldtech.nilapp.api.sponsors.children.SponserCampaign.service.SponsorCampaignService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/sponsor-campaigns")
//@RequiredArgsConstructor
//public class SponsorCampaignController {
//
//    private final SponsorCampaignService service;
//
//    @GetMapping("/sponsor/{sponsorId}")
//    public List<SponsorCampaign> getCampaignsForSponsor(@PathVariable Long sponsorId) {
//        return service.getCampaignsForSponsor(sponsorId);
//    }
//
//    @GetMapping("/entity/{entityId}")
//    public List<SponsorCampaign> getCampaignsForEntity(@PathVariable Long entityId) {
//        return service.getCampaignsForEntity(entityId);
//    }
//
//    @GetMapping("/{campaignId}")
//    public SponsorCampaign getCampaign(@PathVariable Long campaignId) {
//        return service.getCampaign(campaignId);
//    }
//
//    @PostMapping
//    public SponsorCampaign createCampaign(@RequestBody SponsorCampaign campaign) {
//        return service.createCampaign(campaign);
//    }
//
//    @PutMapping("/{campaignId}")
//    public SponsorCampaign updateCampaign(
//            @PathVariable Long campaignId,
//            @RequestBody SponsorCampaign updated
//    ) {
//        return service.updateCampaign(campaignId, updated);
//    }
//
//    @DeleteMapping("/{campaignId}")
//    public void deleteCampaign(@PathVariable Long campaignId) {
//        service.deleteCampaign(campaignId);
//    }
//}
//
