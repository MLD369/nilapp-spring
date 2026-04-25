//package com.mldtech.nilapp.api.achievements.children.AchievementType.controller;
//
//import com.mldtech.nilapp.api.achievements.children.AchievementType.model.AchievementType;
//import com.mldtech.nilapp.api.achievements.children.AchievementType.service.AchievementTypeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/achievement-types")
//@RequiredArgsConstructor
//public class AchievementTypeController {
//
//    private final AchievementTypeService service;
//
//    @GetMapping
////    public List<AchievementType> getAllTypes() {
////        return service.getAllTypes();
////    }
//
//    @GetMapping("/{id}")
//    public AchievementType getType(@PathVariable Long id) {
//        return service.getType(id);
//    }
//
//    @PostMapping
//    public AchievementType createType(@RequestBody AchievementType type) {
//        return service.createType(type);
//    }
//
//    @PutMapping("/{id}")
//    public AchievementType updateType(@PathVariable Long id, @RequestBody AchievementType updated) {
//        return service.updateType(id, updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteType(@PathVariable Long id) {
//        service.deleteType(id);
//    }
//}
//
