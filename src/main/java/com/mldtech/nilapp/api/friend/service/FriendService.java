//package com.mldtech.nilapp.api.friend.service;
//
//
//import com.mldtech.nilapp.api.friend.model.Friend;
//import com.mldtech.nilapp.api.friend.repository.FriendRepository;
//import com.mldtech.nilapp.api.users.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class FriendService {
//
//    private final FriendRepository friendRepository;
//    private final UserRepository userRepository;
//
//    public List<Friend> getFriends(Long userId) {
//        return friendRepository.findByFriendUserId(userId);
//    }
//}
