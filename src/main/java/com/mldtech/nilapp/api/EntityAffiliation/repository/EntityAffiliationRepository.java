package com.mldtech.nilapp.api.EntityAffiliation.repository;

import com.mldtech.nilapp.api.EntityAffiliation.model.EntityAffiliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntityAffiliationRepository extends JpaRepository<EntityAffiliation, Long> {

    List<EntityAffiliation> findByEntities_EntityId(Long entityId);

    List<EntityAffiliation> findByAffiliation_AffiliationId(Long affiliationId);

    boolean existsByEntities_EntityIdAndAffiliation_AffiliationId(Long entityId, Long affiliationId);

    void deleteByEntities_EntityIdAndAffiliation_AffiliationId(Long entityId, Long affiliationId);
}
