package com.mldtech.nilapp.api.group.service;

import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.goals.children.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.group.children.GroupEntity.repository.GroupEntityRepository;
import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.users.children.UserGoal.repository.UserGoalRepository;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
import com.mldtech.nilapp.api.users.dto.*;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import com.mldtech.nilapp.helper.Helper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupEntityRepository groupEntityRepository;
    private final GoalInstanceRepository goalInstanceRepository;
    private final UserGoalRepository userGoalRepository;

    private final Helper helper = new Helper();

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group updateGroup(Long groupId, Group updated) {
        Group existing = getGroup(groupId);

        existing.setName(updated.getName());

        return groupRepository.save(existing);
    }

    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }

    public CustomResponse<List<GroupDTO>> getAllGroupsWithUserStatus(Long userId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var allGroups = groupRepository.findAll();

            // Map: groupId → latest UserGroup membership row
            var membershipMap = user.getUserGroups().stream()
                    .collect(Collectors.toMap(
                            ug -> ug.getGroup().getGroupId(),
                            ug -> ug,
                            (a, b) -> a.getJoinedAt().isAfter(b.getJoinedAt()) ? a : b
                    ));

            var groups = allGroups.stream()
                    .map(g -> {
                        var membership = membershipMap.get(g.getGroupId());

                        boolean joined = membership != null && membership.getLeftAt() == null;

                        return GroupDTO.builder()
                                .groupId(g.getGroupId())
                                .name(g.getName())

                                .joined(joined)
                                .joinedAt(membership != null ? String.valueOf(membership.getJoinedAt()) : null)
                                .leftAt(membership != null ? String.valueOf(membership.getLeftAt()) : null)

                                .build();
                    })
                    .toList();

            return new CustomResponse<>(
                    "Groups fetched successfully with user join status.",
                    groups,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error while fetching groups: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }

    @Transactional
    public CustomResponse<String> joinGroup(Long userId, Long groupId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            // Check if already ACTIVE
            boolean alreadyJoined = user.getUserGroups().stream()
                    .anyMatch(ug -> ug.getGroup().getGroupId().equals(groupId)
                            && ug.getLeftAt() == null);

            if (alreadyJoined) {
                return new CustomResponse<>(
                        "User already joined this group.",
                        null,
                        HttpStatus.CONFLICT,
                        "409"
                );
            }

            // Create new membership
            UserGroup userGroup = new UserGroup();
            userGroup.setUser(user);
            userGroup.setGroup(group);
            userGroup.setJoinedAt(Instant.now());
            userGroup.setLeftAt(null);

            userGroupRepository.save(userGroup);

            return new CustomResponse<>(
                    "User successfully joined the group.",
                    null,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error joining group: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }

    @Transactional
    public CustomResponse<String> leaveGroup(Long userId, Long groupId) {
        try {
            var membership = userGroupRepository.findActiveMembership(userId, groupId);

            if (membership == null) {
                return new CustomResponse<>(
                        "User is not currently a member of this group.",
                        null,
                        HttpStatus.NOT_FOUND,
                        "404"
                );
            }

            membership.setLeftAt(Instant.now());
            userGroupRepository.save(membership);

            return new CustomResponse<>(
                    "User successfully left the group.",
                    null,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error leaving group: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }
    public CustomResponse<GroupDetailsDTO> getGroupDetails(Long groupId, Long userId, boolean includeUserGoals) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // 1. SUBGROUPS (empty for now)
        List<SubGroupDTO> subGroups = List.of();

        // 2. AFFILIATED ENTITIES
        var groupEntities = groupEntityRepository.findByGroup_GroupId(groupId);

        List<EntityDetailsDTO> affiliatedEntities = groupEntities.stream()
                .map(ge -> {
                    Entities e = ge.getEntities();
                    return EntityDetailsDTO.builder()
                            .entityId(e.getEntityId())
                            .name(e.getName())
                            .abbreviation(e.getAbbreviation())
                            .associatedSchool(e.getAssociatedSchool())
                            .entityType(
                                    EntityTypeDTO.builder()
                                            .entityTypesId(e.getEntityType().getEntityTypesId())
                                            .type(e.getEntityType().getType())
                                            .description(e.getEntityType().getDescription())
                                            .build()
                            )
                            .build();
                })
                .toList();

        /*
         * ============================================================
         * 3. ACTIVE GOALS FOR THIS GROUP
         * ============================================================
         */
        var goalInstances = goalInstanceRepository.findByGroup_GroupId(groupId);

        // Preload user joined goal instance IDs
        Set<Long> userJoinedGoalInstanceIds;

        if (includeUserGoals && userId != null) {
            userJoinedGoalInstanceIds = userGoalRepository.findByUser_UserId(userId).stream()
                    .filter(ug -> ug.getLeftAt() == null)
                    .map(ug -> ug.getGoalInstance().getGoalInstanceId())
                    .collect(Collectors.toSet());
        } else {
            userJoinedGoalInstanceIds = new HashSet<>();
        }

        List<GoalProgressDTO> activeGoals = goalInstances.stream()
                .map(gi -> {
                    GoalProgressDTO dto = helper.toGoalProgressDTO(gi);

                    // ⭐ Correct: set groupId, not entityId
                    dto.setGroupId(groupId);

                    // ⭐ Add joined flag
                    dto.setJoined(userJoinedGoalInstanceIds.contains(gi.getGoalInstanceId()));

                    return dto;
                })
                .toList();

        /*
         * ============================================================
         * FINAL DTO
         * ============================================================
         */
        GroupDetailsDTO dto = GroupDetailsDTO.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .subGroups(subGroups)
                .affiliatedEntities(affiliatedEntities)
                .activeGoals(activeGoals)
                .build();

        return new CustomResponse<>(dto, HttpStatus.OK, "200");
    }




}
