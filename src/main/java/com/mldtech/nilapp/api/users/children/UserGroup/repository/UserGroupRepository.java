package com.mldtech.nilapp.api.users.children.UserGroup.repository;

import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
//    List<UserGroup> findByUserGroupUserId(Long userId);
    List<UserGroup> findByUser_UserId(Long userId);

    @Query("SELECT ug FROM UserGroup ug WHERE ug.user.userId = :userId AND ug.group.groupId = :groupId AND ug.leftAt IS NULL")
    UserGroup findActiveMembership(Long userId, Long groupId);


}

