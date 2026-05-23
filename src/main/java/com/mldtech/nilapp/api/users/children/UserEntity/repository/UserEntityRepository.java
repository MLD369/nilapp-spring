package com.mldtech.nilapp.api.users.children.UserEntity.repository;


import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByUserUserId(Long userId);

    boolean existsByUserUserIdAndEntityEntityId(Long userId, Long entityId);

    void deleteByUserUserIdAndEntityEntityId(Long userId, Long entityId);
    List<UserEntity> findByUser_UserId(Long userId);
    @Query("SELECT ue FROM UserEntity ue WHERE ue.user.userId = :userId AND ue.entity.entityId = :entityId AND ue.leftAt IS NULL")
    UserEntity findActiveMembership(Long userId, Long entityId);

}

