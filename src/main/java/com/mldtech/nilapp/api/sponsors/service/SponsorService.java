package com.mldtech.nilapp.api.sponsors.service;

import com.mldtech.nilapp.api.sponsors.model.Sponsor;
import com.mldtech.nilapp.api.sponsors.repository.SponsorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorService {

    private final SponsorRepository repository;

    public List<Sponsor> getAllSponsors() {
        return repository.findAll();
    }

    public List<Sponsor> getActiveSponsors() {
        return repository.findByIsActiveTrue();
    }

    public Sponsor getSponsor(Long sponsorId) {
        return repository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
    }

    public Sponsor createSponsor(Sponsor sponsor) {
        if (repository.existsByName(sponsor.getName())) {
            throw new RuntimeException("Sponsor already exists");
        }
        return repository.save(sponsor);
    }

    public Sponsor updateSponsor(Long sponsorId, Sponsor updated) {
        Sponsor existing = getSponsor(sponsorId);

        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setContactEmail(updated.getContactEmail());
        existing.setIsActive(updated.getIsActive());

        return repository.save(existing);
    }

    public void deleteSponsor(Long sponsorId) {
        repository.deleteById(sponsorId);
    }
}

