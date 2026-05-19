package com.mldtech.nilapp.api.groupstat.repository;

import com.mldtech.nilapp.api.groupstat.model.GroupStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupStatRepository extends JpaRepository<GroupStat, Long> {
    List<GroupStat> findByUserIdAndGroupId(Long userId, Long groupId);
    List<GroupStat> findByGroupId(Long groupId);
}
