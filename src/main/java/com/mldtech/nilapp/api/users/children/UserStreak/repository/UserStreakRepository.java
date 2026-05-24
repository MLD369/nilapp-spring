package com.mldtech.nilapp.api.users.children.UserStreak.repository;

import com.mldtech.nilapp.api.users.children.UserStreak.model.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {

    @Query("""
    SELECT u.userId, u.username, s.currentStreak
    FROM UserStreak s
    JOIN User u ON u.userId = s.user.userId
    ORDER BY s.currentStreak DESC
""")
    List<Object[]> getTopStreaks();



}

