package com.mldtech.nilapp.api.EntityType.repository;

import com.mldtech.nilapp.api.EntityType.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityTypeRepository extends JpaRepository<EntityType, Long> {

    boolean existsByType(String type);
}
