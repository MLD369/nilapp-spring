//package com.mldtech.nilapp.api.entities.children.EntityType.controller;
//
//import com.mldtech.nilapp.api.entities.children.EntityType.model.EntityType;
//import com.mldtech.nilapp.api.entities.children.EntityType.service.EntityTypeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/entity-types")
//@RequiredArgsConstructor
//public class EntityTypeController {
//
//    private final EntityTypeService service;
//
//    @GetMapping
//    public List<EntityType> getAllTypes() {
//        return service.getAllTypes();
//    }
//
//    @PostMapping
//    public EntityType createType(@RequestBody EntityType type) {
//        return service.createType(type);
//    }
//}
//
