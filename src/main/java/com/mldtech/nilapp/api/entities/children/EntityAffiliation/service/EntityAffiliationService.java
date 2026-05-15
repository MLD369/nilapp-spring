package com.mldtech.nilapp.api.entities.children.EntityAffiliation.service;

import com.mldtech.nilapp.api.affiliations.model.Affiliation;
import com.mldtech.nilapp.api.affiliations.repository.AffiliationRepository;
import com.mldtech.nilapp.api.entities.children.EntityAffiliation.model.EntityAffiliation;
import com.mldtech.nilapp.api.entities.children.EntityAffiliation.repository.EntityAffiliationRepository;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityAffiliationService {

    private final EntityAffiliationRepository repository;
    private final EntityRepository entityRepository;
    private final AffiliationRepository affiliationRepository;

    public List<EntityAffiliation> getAffiliationsForEntity(Long entityId) {
        return repository.findByEntities_EntityId(entityId);
    }

    public EntityAffiliation addAffiliationToEntity(Long entityId, Long affiliationId) {

        if (repository.existsByEntities_EntityIdAndAffiliation_AffiliationId(entityId, affiliationId)) {
            throw new RuntimeException("Affiliation already assigned to entity");
        }

        Entities entity = entityRepository.findById(entityId)
                .orElseThrow(() -> new RuntimeException("Entity not found"));

        Affiliation affiliation = affiliationRepository.findById(affiliationId)
                .orElseThrow(() -> new RuntimeException("Affiliation not found"));

        EntityAffiliation ea = EntityAffiliation.builder()
                .entities(entity)
                .affiliation(affiliation)
//                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(ea);
    }

    public void removeAffiliationFromEntity(Long entityId, Long affiliationId) {
        repository.deleteByEntities_EntityIdAndAffiliation_AffiliationId(entityId, affiliationId);
    }
}
