//package com.mldtech.nilapp.api.users.children.UserEntity.controller;
//
//import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
//import com.mldtech.nilapp.api.users.children.UserEntity.service.UserEntityService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/v1/user-entities")
//@RequiredArgsConstructor
//public class UserEntityController {
//
//    private final UserEntityService service;
//
//    @GetMapping("/user/{userId}")
//    public List<UserEntity> getEntitiesForUser(@PathVariable Long userId) {
//        return service.getEntitiesForUser(userId);
//    }
//
//}
//
