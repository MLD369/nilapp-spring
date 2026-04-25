package com.mldtech.nilapp.api.entities.controller;

import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.entities.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntityController {

    private final EntityService service;

    @GetMapping
    public List<Entities> getAllEntities() {
        return service.getAllEntities();
    }

    @GetMapping("/{entityId}")
    public Entities getEntity(@PathVariable Long entityId) {
        return service.getEntities(entityId);
    }

    @PostMapping
    public Entities createEntity(@RequestBody Entities entity) {
        return service.createEntities(entity);
    }

    @PutMapping("/{entityId}")
    public Entities updateEntity(@PathVariable Long entityId, @RequestBody Entities updated) {
        return service.updateEntities(entityId, updated);
    }

    @DeleteMapping("/{entityId}")
    public void deleteEntity(@PathVariable Long entityId) {
        service.deleteEntities(entityId);
    }
}

