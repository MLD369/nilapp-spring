//package com.mldtech.nilapp.api.users.children.UserGroup.controller;
//
//import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
//import com.mldtech.nilapp.api.users.children.UserGroup.service.UserGroupService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/user-groups")
//@RequiredArgsConstructor
//public class UserGroupController {
//
//    private final UserGroupService service;
//
//    @GetMapping("/user/{userId}")
//    public List<UserGroup> getGroupsForUser(@PathVariable Long userId) {
//        return service.getGroupsForUser(userId);
//    }
//}
//
