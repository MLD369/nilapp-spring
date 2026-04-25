//package com.mldtech.nilapp.api.affiliations.controller;
//
//import com.mldtech.nilapp.api.affiliations.model.Affiliation;
//import com.mldtech.nilapp.api.affiliations.service.AffiliationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/affiliations")
//@RequiredArgsConstructor
//public class AffiliationController {
//
//    private final AffiliationService service;
//
//    @GetMapping
//    public List<Affiliation> getAllAffiliations() {
//        return service.getAllAffiliations();
//    }
//
//    @GetMapping("/{id}")
//    public Affiliation getAffiliation(@PathVariable Long id) {
//        return service.getAffiliation(id);
//    }
//
//    @PostMapping
//    public Affiliation createAffiliation(@RequestBody Affiliation affiliation) {
//        return service.createAffiliation(affiliation);
//    }
//
//    @PutMapping("/{id}")
//    public Affiliation updateAffiliation(
//            @PathVariable Long id,
//            @RequestBody Affiliation updated
//    ) {
//        return service.updateAffiliation(id, updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteAffiliation(@PathVariable Long id) {
//        service.deleteAffiliation(id);
//    }
//}
