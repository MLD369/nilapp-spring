package com.mldtech.nilapp.api.entities.children.EntityType.service;

import com.mldtech.nilapp.api.entities.children.EntityType.model.EntityType;
import com.mldtech.nilapp.api.entities.children.EntityType.repository.EntityTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityTypeService {

    private final EntityTypeRepository repository;

    public List<EntityType> getAllEntityTypes() {
        return repository.findAll();
    }

    public EntityType getEntityType(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EntityType not found"));
    }

    public EntityType createEntityType(EntityType type) {

        if (repository.existsByType(type.getType())) {
            throw new RuntimeException("EntityType already exists");
        }

        return repository.save(type);
    }

    public EntityType updateEntityType(Long id, EntityType updated) {
        EntityType existing = getEntityType(id);

        existing.setType(updated.getType());

        return repository.save(existing);
    }

    public void deleteEntityType(Long id) {
        repository.deleteById(id);
    }
}
