package com.mldtech.nilapp.api.group.children.GroupEntity.repository;

import com.mldtech.nilapp.api.group.children.GroupEntity.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupEntityRepository extends JpaRepository<GroupEntity, Long> {
    List<GroupEntity> findByGroup_GroupId(Long groupId);
}

