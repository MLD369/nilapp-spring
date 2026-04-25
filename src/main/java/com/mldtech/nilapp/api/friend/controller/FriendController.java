//package com.mldtech.nilapp.api.friend.controller;
//
//import com.mldtech.nilapp.api.friend.model.Friend;
//import com.mldtech.nilapp.api.friend.service.FriendService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/friends")
//@RequiredArgsConstructor
//public class FriendController {
//
//    private final FriendService service;
//
//    @GetMapping("/{userId}")
//    public List<Friend> getFriendsForUser(@PathVariable Long userId) {
//        return service.getFriends(userId);
//    }
//
//
//}
