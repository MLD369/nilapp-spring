package com.mldtech.nilapp.api.GoalInstances.service;

import com.mldtech.nilapp.api.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.goals.model.Goal;
import com.mldtech.nilapp.api.goals.repository.GoalRepository;
import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.helper.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoalInstanceService {
    // Replace 1L with your master default goal's ID in public.goals
    public static final Long DEFAULT_POT_GOAL_ID = 0L;

    private final GoalInstanceRepository goalInstanceRepository;
    private final GoalRepository goalRepository;

    public CustomResponse<?> create(Long goalId, GoalInstance instance) {
        instance.setGoal(goalRepository.getReferenceById(goalId));
        return new CustomResponse<>(goalInstanceRepository.save(instance), HttpStatus.OK, "200");
    }

    public CustomResponse<?> getAll() {
        return new CustomResponse<>(goalInstanceRepository.findAll(), HttpStatus.OK, "200");
    }



    @Transactional
    public void createDefaultInstanceForEntity(Entities entity) {
        Goal defaultGoal = goalRepository.getReferenceById(DEFAULT_POT_GOAL_ID);
        goalInstanceRepository.save(buildPermanentInstance(defaultGoal, entity, null));
    }

    @Transactional
    public void createDefaultInstanceForGroup(Group group) {
        Goal defaultGoal = goalRepository.getReferenceById(DEFAULT_POT_GOAL_ID);
        goalInstanceRepository.save(buildPermanentInstance(defaultGoal, null, group));
    }

    private GoalInstance buildPermanentInstance(Goal defaultGoal, Entities entity, Group group) {
        return GoalInstance.builder()
                .goal(defaultGoal)
                .entity(entity)
                .group(group)
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(9999, 12, 31))
                .isActive(true)
                .isComplete(false)
                .stepsContributed(0L)
                .coinsContributed(0L)
                .stepsNeeded(null)
                .coinsNeeded(null)
                .dollarAmount(BigDecimal.ZERO)
                .status(1) // Match your active pacing_status_id
                .build();
    }

}
