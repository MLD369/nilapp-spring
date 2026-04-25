package com.mldtech.nilapp.api.sponsors.repository;

import com.mldtech.nilapp.api.sponsors.model.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

    List<Sponsor> findByIsActiveTrue();

    List<Sponsor> findByType(String type);

    boolean existsByName(String name);
}

