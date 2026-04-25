//package com.mldtech.nilapp.api.entities.children.EntityAffiliation.service;
//
//import com.mldtech.nilapp.api.entities.children.EntityAffiliation.model.EntityAffiliation;
//import com.mldtech.nilapp.api.entities.children.EntityAffiliation.repository.EntityAffiliationRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class EntityAffiliationService {
//
//    private final EntityAffiliationRepository repository;
//
//    public List<EntityAffiliation> getAffiliationsForEntity(Long entityId) {
//        return repository.findByIdEntityId(entityId);
//    }
//
//    public EntityAffiliation addAffiliation(Long entityId, Long affiliationId) {
//
//        EntityAffiliationId id = new EntityAffiliationId(entityId, affiliationId);
//
//        if (repository.existsByIdEntityIdAndIdAffiliationId(entityId, affiliationId)) {
//            return repository.findById(id).orElseThrow();
//        }
//
//        EntityAffiliation ea = EntityAffiliation.builder()
//                .id(id)
//                .build();
//
//        return repository.save(ea);
//    }
//
//    public void removeAffiliation(Long entityId, Long affiliationId) {
//        repository.deleteByIdEntityIdAndIdAffiliationId(entityId, affiliationId);
//    }
//}
//
