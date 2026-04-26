package com.mldtech.nilapp.api.users.children.UserEntity.service;


//import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserEntityService {

        private final UserEntityRepository repo;
        private final UserRepository userRepo;
        private final EntityRepository entityRepo;


        public List<UserEntity> setUserEntities(Long userId, List<Long> entityIds) {

            var user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 1. Remove all existing entities
            var existing = repo.findByUserUserId(userId);
            repo.deleteAll(existing);

            // 2. Add new ones
            var newEntities = entityIds.stream()
                    .map(entityId -> UserEntity.builder()
                            .user(user)
                            .entity(entityRepo.getReferenceById(entityId))
                            .build()
                    )
                    .toList();

            return repo.saveAll(newEntities);
        }


    // ADD ENTITY TO USER
    public UserEntity addEntity(Long userId, Long entityId) {

        if (repo.existsByUserUserIdAndEntityEntityId(userId, entityId)) {
            throw new RuntimeException("User already has this entity");
        }

        var user = userRepo.getReferenceById(userId);
        var entity = entityRepo.getReferenceById(entityId);

        UserEntity ue = UserEntity.builder()
                .user(user)
                .entity(entity)
                .build();

        return repo.save(ue);
    }

    // REMOVE ENTITY FROM USER
    @Transactional
    public void removeEntity(Long userId, Long entityId) {

        if (!repo.existsByUserUserIdAndEntityEntityId(userId, entityId)) {
            throw new RuntimeException("User does not have this entity");
        }

        repo.deleteByUserUserIdAndEntityEntityId(userId, entityId);
    }
}

//    private final UserEntityRepository repo;
//    private final UserRepository userRepo;
//    private final EntityRepository entityRepo;

//    public List<UserEntity> getEntitiesForUser(Long userId) {
//        return repo.findByUserEntitiesByUserId(userId);
//    }

//    public UserEntity addEntityToUser(Long userId, Long entityId) {
//
//        UserEntity ue = UserEntity.builder()
//                .user(userRepo.getReferenceById(userId))
//                .entity(entityRepo.getReferenceById(entityId))
//                .build();
//
//        return repo.save(ue);
//    }



