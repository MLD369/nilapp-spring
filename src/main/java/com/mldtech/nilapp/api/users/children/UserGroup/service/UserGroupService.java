//package com.mldtech.nilapp.api.users.children.UserGroup.service;
//
//
//import com.mldtech.nilapp.api.users.children.UserGroup.model.UserGroup;
//import com.mldtech.nilapp.api.users.children.UserGroup.repository.UserGroupRepository;
//import com.mldtech.nilapp.api.users.repository.UserRepository;
////import com.mldtech.nilapp.api.group.repository.GroupRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserGroupService {
//
//    private final UserGroupRepository repo;
//    private final UserRepository userRepo;
////    private final GroupRepository groupRepo;
//
//    public List<UserGroup> getGroupsForUser(Long userId) {
//        return repo.findByUserGroupUserId(userId);
//    }
//
//    public UserGroup addUserToGroup(Long userId, Long groupId) {
//
//        UserGroup ug = UserGroup.builder()
//                .user(userRepo.getReferenceById(userId))
////                .group(groupRepo.getReferenceById(groupId))
//                .build();
//
//        return repo.save(ug);
//    }
//}
//
