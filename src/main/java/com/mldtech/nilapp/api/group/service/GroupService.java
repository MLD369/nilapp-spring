package com.mldtech.nilapp.api.group.service;

import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
import com.mldtech.nilapp.api.users.dto.GroupDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

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

            var userGroupIds = user.getUserGroups().stream()
                    .map(ug -> ug.getGroup().getGroupId())
                    .collect(Collectors.toSet());

            var groups = allGroups.stream()
                    .map(g -> GroupDTO.builder()
                            .groupId(g.getGroupId())
                            .name(g.getName())
                            .joined(userGroupIds.contains(g.getGroupId()))
                            .build())
                    .toList();

            return new CustomResponse<>(
                    "Groups fetched successfully with user join status.",
                    groups,
                    HttpStatus.OK,
                    String.valueOf(HttpStatus.OK.value())
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error while fetching groups: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
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

            boolean alreadyJoined = user.getUserGroups().stream()
                    .anyMatch(ug -> ug.getGroup().getGroupId().equals(groupId));

            if (alreadyJoined) {
                return new CustomResponse<>(
                        "User already joined this group.",
                        null,
                        HttpStatus.CONFLICT,
                        String.valueOf(HttpStatus.CONFLICT.value())
                );
            }

            UserGroup userGroup = new UserGroup();
            userGroup.setUser(user);
            userGroup.setGroup(group);

            userGroupRepository.save(userGroup);

            return new CustomResponse<>(
                    "User successfully joined the group.",
                    null,
                    HttpStatus.OK,
                    String.valueOf(HttpStatus.OK.value())
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error joining group: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
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


}
