package com.mldtech.nilapp.api.entities.repository;

import com.mldtech.nilapp.api.entities.model.Entities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<Entities, Long> {
}
