package com.mldtech.nilapp.api.contributions.repository;

import com.mldtech.nilapp.api.contributions.model.Contribution;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByUserId(Long userId);

    List<Contribution> findByEntityId(Long entityId);

    List<Contribution> findByCampaignId(Long campaignId);

//    List<Contribution> findByStatusId(Integer statusId);
    List<Contribution> findByUserIdAndEntityId(Long userId, Long entityId);


}

