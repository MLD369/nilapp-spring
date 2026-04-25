//package com.mldtech.nilapp.api.affiliations.service;
//
//import com.mldtech.nilapp.api.affiliations.model.Affiliation;
//import com.mldtech.nilapp.api.affiliations.repository.AffiliationRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class AffiliationService {
//
//    private final AffiliationRepository repository;
//
//    public List<Affiliation> getAllAffiliations() {
//        return repository.findAll();
//    }
//
//    public Affiliation getAffiliation(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Affiliation not found"));
//    }
//
//    public Affiliation createAffiliation(Affiliation affiliation) {
//        if (repository.existsByName(affiliation.getName())) {
//            throw new RuntimeException("Affiliation already exists");
//        }
//        return repository.save(affiliation);
//    }
//
//    public Affiliation updateAffiliation(Long id, Affiliation updated) {
//        Affiliation existing = getAffiliation(id);
//        existing.setName(updated.getName());
//        return repository.save(existing);
//    }
//
//    public void deleteAffiliation(Long id) {
//        repository.deleteById(id);
//    }
//}
//
