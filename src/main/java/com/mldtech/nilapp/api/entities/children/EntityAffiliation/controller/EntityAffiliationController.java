//package com.mldtech.nilapp.api.entities.children.EntityAffiliation.controller;
//
//import com.mldtech.nilapp.api.entities.children.EntityAffiliation.model.EntityAffiliation;
//import com.mldtech.nilapp.api.entities.children.EntityAffiliation.service.EntityAffiliationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/entity-affiliations")
//@RequiredArgsConstructor
//public class EntityAffiliationController {
//
//    private final EntityAffiliationService service;
//
//    @GetMapping("/{entityId}")
//    public List<EntityAffiliation> getAffiliationsForEntity(@PathVariable Long entityId) {
//        return service.getAffiliationsForEntity(entityId);
//    }
//
//    @PostMapping("/{entityId}/{affiliationId}")
//    public EntityAffiliation addAffiliation(
//            @PathVariable Long entityId,
//            @PathVariable Long affiliationId
//    ) {
//        return service.addAffiliation(entityId, affiliationId);
//    }
//
//    @DeleteMapping("/{entityId}/{affiliationId}")
//    public void removeAffiliation(
//            @PathVariable Long entityId,
//            @PathVariable Long affiliationId
//    ) {
//        service.removeAffiliation(entityId, affiliationId);
//    }
//}
