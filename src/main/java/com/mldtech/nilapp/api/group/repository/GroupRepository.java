package com.mldtech.nilapp.api.group.repository;

import com.mldtech.nilapp.api.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}

