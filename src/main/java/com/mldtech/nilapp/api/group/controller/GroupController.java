package com.mldtech.nilapp.api.group.controller;

import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService service;

    @GetMapping
    public List<Group> getAllGroups() {
        return service.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public Group getGroup(@PathVariable Long groupId) {
        return service.getGroup(groupId);
    }

    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        return service.createGroup(group);
    }

    @PutMapping("/{groupId}")
    public Group updateGroup(@PathVariable Long groupId, @RequestBody Group updated) {
        return service.updateGroup(groupId, updated);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        service.deleteGroup(groupId);
    }
}

