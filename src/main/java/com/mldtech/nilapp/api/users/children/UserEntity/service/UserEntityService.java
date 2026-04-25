//package com.mldtech.nilapp.api.users.children.UserEntity.service;
//
//
////import com.mldtech.nilapp.api.entities.repository.EntityRepository;
//import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
//import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
//import com.mldtech.nilapp.api.users.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserEntityService {
//
//    private final UserEntityRepository repo;
//    private final UserRepository userRepo;
////    private final EntityRepository entityRepo;
//
//    public List<UserEntity> getEntitiesForUser(Long userId) {
//        return repo.findByUserEntitiesByUserId(userId);
//    }
//
////    public UserEntity addEntityToUser(Long userId, Long entityId) {
////
////        UserEntity ue = UserEntity.builder()
////                .user(userRepo.getReferenceById(userId))
////                .entity(entityRepo.getReferenceById(entityId))
////                .build();
////
////        return repo.save(ue);
////    }
//}
//
//
