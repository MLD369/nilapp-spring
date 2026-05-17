package com.mldtech.nilapp.api.friend.controller;

import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.friend.service.FriendService;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService service;
    @GetMapping()
    public ResponseEntity<CustomResponse<?>> getAllFriend() {
        var result = service.getAllFriends();
        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }

    @PostMapping("/{userId}/request/{friendId}")
    public ResponseEntity<CustomResponse<?>> sendRequest(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        var result = service.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }

    @PostMapping("/{userId}/accept/{friendId}")
    public ResponseEntity<CustomResponse<?>> accept(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        var result = service.acceptFriendRequest(userId, friendId);
        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }

    @PostMapping("/{userId}/unfriend/{friendId}")
    public ResponseEntity<CustomResponse<?>> unfriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        var result = service.unfriend(userId, friendId);
        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }

    @PostMapping("/{userId}/block/{friendId}")
    public ResponseEntity<CustomResponse<?>> blockUser(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        var result = service.blockUser(userId, friendId);
        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }

    @PostMapping("/{userId}/unblock/{friendId}")
    public ResponseEntity<CustomResponse<?>> unblockUser(
            @PathVariable Long userId,
            @PathVariable Long friendId,
            @RequestParam(defaultValue = "false") boolean refriend
    ) {
        var result = service.unblockUser(userId, friendId, refriend);

        if (result == null) {
            return ResponseEntity.ok(
                    new CustomResponse<>("User unblocked", HttpStatus.OK, "200")
            );
        }

        return ResponseEntity.ok(new CustomResponse<>(result, HttpStatus.OK, "200"));
    }
}
