package com.mldtech.nilapp.api.entities.children.EntityType.repository;

import com.mldtech.nilapp.api.entities.children.EntityType.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityTypeRepository extends JpaRepository<EntityType, Long> {

    boolean existsByType(String type);
}
