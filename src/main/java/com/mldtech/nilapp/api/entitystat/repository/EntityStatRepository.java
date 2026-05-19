package com.mldtech.nilapp.api.entitystat.repository;
import com.mldtech.nilapp.api.entitystat.model.EntityStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface EntityStatRepository extends JpaRepository<EntityStat, Long> {
    List<EntityStat> findByUserIdAndEntityId(Long userId, Long entityId);
    List<EntityStat> findByEntityId(Long entityId);
}

