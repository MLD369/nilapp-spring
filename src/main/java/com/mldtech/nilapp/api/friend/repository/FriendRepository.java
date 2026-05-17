package com.mldtech.nilapp.api.friend.repository;

import com.mldtech.nilapp.api.friend.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByUserUserIdAndFriendUserId(Long userId, Long friendId);

    Friend findByUserUserIdAndFriendUserId(Long userId, Long friendId);

    List<Friend> findByUserUserId(Long userId);

    void deleteByUserUserIdAndFriendUserId(Long userId, Long friendId);

    List<Friend> findByFriendUserId(Long userId);


}


