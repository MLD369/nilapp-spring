package com.mldtech.nilapp.api.entities.service;

import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final EntityRepository repository;

    public List<Entities> getAllEntities() {
        return repository.findAll();
    }

    public Entities getEntities(Long EntitiesId) {
        return repository.findById(EntitiesId)
                .orElseThrow(() -> new RuntimeException("Entities not found"));
    }

    public Entities createEntities(Entities Entities) {
        return repository.save(Entities);
    }

    public Entities updateEntities(Long EntitiesId, Entities updated) {
        Entities existing = getEntities(EntitiesId);

        existing.setName(updated.getName());
        existing.setAbbreviation(updated.getAbbreviation());
        existing.setAssociatedSchool(updated.getAssociatedSchool());
        existing.setEntityType(updated.getEntityType());
        existing.setIsActive(updated.getIsActive());
        existing.setPrimaryColor(updated.getPrimaryColor());
        existing.setSecondaryColor(updated.getSecondaryColor());
        existing.setSportingAffiliations(updated.getSportingAffiliations());
        existing.setTaxId(updated.getTaxId());

        return repository.save(existing);
    }

    public void deleteEntities(Long EntitiesId) {
        repository.deleteById(EntitiesId);
    }
}

