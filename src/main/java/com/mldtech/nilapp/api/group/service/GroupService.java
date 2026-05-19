package com.mldtech.nilapp.api.group.service;

import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository repository;

    public List<Group> getAllGroups() {
        return repository.findAll();
    }

    public Group getGroup(Long groupId) {
        return repository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public Group createGroup(Group group) {
        return repository.save(group);
    }

    public Group updateGroup(Long groupId, Group updated) {
        Group existing = getGroup(groupId);

        existing.setName(updated.getName());

        return repository.save(existing);
    }

    public void deleteGroup(Long groupId) {
        repository.deleteById(groupId);
    }
}
