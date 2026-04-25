//package com.mldtech.nilapp.api.achievements.controller;
//
//import com.mldtech.nilapp.api.achievements.model.Achievement;
//import com.mldtech.nilapp.api.achievements.service.AchievementService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/achievements")
//@RequiredArgsConstructor
//public class AchievementController {
//
//    private final AchievementService service;
//
//    @GetMapping
//    public List<Achievement> getAllAchievements() {
//        return service.getAllAchievements();
//    }
//
//    @GetMapping("/{id}")
//    public Achievement getAchievement(@PathVariable Long id) {
//        return service.getAchievement(id);
//    }
//
//    @GetMapping("/type/{typeId}")
//    public List<Achievement> getAchievementsByType(@PathVariable Long typeId) {
//        return service.getAchievementsByType(typeId);
//    }
//
//    @PostMapping
//    public Achievement createAchievement(@RequestBody Achievement achievement) {
//        return service.createAchievement(achievement);
//    }
//
//    @PutMapping("/{id}")
//    public Achievement updateAchievement(@PathVariable Long id, @RequestBody Achievement updated) {
//        return service.updateAchievement(id, updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteAchievement(@PathVariable Long id) {
//        service.deleteAchievement(id);
//    }
//}
