//package com.mldtech.nilapp.api.friend.repository;
//
//import com.mldtech.nilapp.api.friend.model.Friend;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface FriendRepository extends JpaRepository<Friend, Long> {
//
//    List<Friend> findByUserUserId(Long userId);
//
//    List<Friend> findByFriendUserId(Long userId);
//
//    boolean existsByUserUserIdAndFriendUserId(Long userId, Long friendId);
//}
//
