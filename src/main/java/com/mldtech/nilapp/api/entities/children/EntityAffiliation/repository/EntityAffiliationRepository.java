//package com.mldtech.nilapp.api.entities.children.EntityAffiliation.repository;
//
//import com.mldtech.nilapp.api.entities.children.EntityAffiliation.model.EntityAffiliation;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface EntityAffiliationRepository extends JpaRepository<EntityAffiliation, EntityAffiliationId> {
//
//    List<EntityAffiliation> findByIdEntityId(Long entityId);
//
//    List<EntityAffiliation> findByIdAffiliationId(Long affiliationId);
//
//    boolean existsByIdEntityIdAndIdAffiliationId(Long entityId, Long affiliationId);
//
//    void deleteByIdEntityIdAndIdAffiliationId(Long entityId, Long affiliationId);
//}
