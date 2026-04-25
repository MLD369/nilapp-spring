//package com.mldtech.nilapp.api.friend.children.FriendStatus.controller;
//
//import com.mldtech.nilapp.api.friend.children.FriendStatus.model.FriendStatus;
//import com.mldtech.nilapp.api.friend.children.FriendStatus.service.FriendStatusService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/friend-statuses")
//@RequiredArgsConstructor
//public class FriendStatusController {
//
//    private final FriendStatusService service;
//
//    @GetMapping
//    public List<FriendStatus> getAllStatuses() {
//        return service.getAllStatuses();
//    }
//
//    @PostMapping
//    public FriendStatus createStatus(@RequestBody FriendStatus status) {
//        return service.createStatus(status);
//    }
//}
