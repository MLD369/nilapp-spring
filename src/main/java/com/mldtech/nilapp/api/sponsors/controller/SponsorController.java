package com.mldtech.nilapp.api.sponsors.controller;

import com.mldtech.nilapp.api.sponsors.model.Sponsor;
import com.mldtech.nilapp.api.sponsors.service.SponsorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sponsors")
@RequiredArgsConstructor
public class SponsorController {

    private final SponsorService service;

    @GetMapping
    public List<Sponsor> getAllSponsors() {
        return service.getAllSponsors();
    }

    @GetMapping("/active")
    public List<Sponsor> getActiveSponsors() {
        return service.getActiveSponsors();
    }

    @GetMapping("/{sponsorId}")
    public Sponsor getSponsor(@PathVariable Long sponsorId) {
        return service.getSponsor(sponsorId);
    }

    @PostMapping
    public Sponsor createSponsor(@RequestBody Sponsor sponsor) {
        return service.createSponsor(sponsor);
    }

    @PutMapping("/{sponsorId}")
    public Sponsor updateSponsor(
            @PathVariable Long sponsorId,
            @RequestBody Sponsor updated
    ) {
        return service.updateSponsor(sponsorId, updated);
    }

    @DeleteMapping("/{sponsorId}")
    public void deleteSponsor(@PathVariable Long sponsorId) {
        service.deleteSponsor(sponsorId);
    }
}

