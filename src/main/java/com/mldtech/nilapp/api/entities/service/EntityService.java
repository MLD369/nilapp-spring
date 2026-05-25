package com.mldtech.nilapp.api.entities.service;

import com.mldtech.nilapp.api.entities.model.Entities;
import com.mldtech.nilapp.api.entities.repository.EntityRepository;
import com.mldtech.nilapp.api.users.children.UserEntity.model.UserEntity;
import com.mldtech.nilapp.api.users.children.UserEntity.repository.UserEntityRepository;
import com.mldtech.nilapp.api.users.dto.EntityDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import com.mldtech.nilapp.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final EntityRepository entityRepository;
    private final UserRepository userRepository;
    private final UserEntityRepository userEntityRepository;

    public CustomResponse<List<EntityDTO>> getAllEntities() {
        try {
            List<EntityDTO> entities = entityRepository.findAll()
                    .stream()
                    .map(EntityMapper::toDTO)
                    .toList();

            if (entities.isEmpty()) {
                return new CustomResponse<>(
                        "No entities found in the database.",
                        null,
                        HttpStatus.NO_CONTENT,
                        String.valueOf(HttpStatus.NO_CONTENT.value())
                );
            }

            return new CustomResponse<>(
                    "Entities fetched successfully.",
                    entities,
                    HttpStatus.OK,
                    String.valueOf(HttpStatus.OK.value())
            );

        } catch (IllegalArgumentException ex) {
            return new CustomResponse<>(
                    "Invalid request: " + ex.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            );

        } catch (org.springframework.dao.DataAccessException ex) {
            return new CustomResponse<>(
                    "Database error occurred while fetching entities.",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }




    public Entities getEntities(Long EntitiesId) {
        return entityRepository.findById(EntitiesId)
                .orElseThrow(() -> new RuntimeException("Entities not found"));
    }

    public Entities createEntities(Entities Entities) {
        return entityRepository.save(Entities);
    }

    public Entities updateEntities(Long EntitiesId, Entities updated) {
        Entities existing = getEntities(EntitiesId);

        existing.setName(updated.getName());
        existing.setAbbreviation(updated.getAbbreviation());
        existing.setAssociatedSchool(updated.getAssociatedSchool());
        existing.setEntityType(updated.getEntityType());
        existing.setIsActive(updated.getIsActive());
        existing.setPrimaryColor(updated.getPrimaryColor());
        existing.setSecondaryColor(updated.getSecondaryColor());
        existing.setSportingAffiliations(updated.getSportingAffiliations());
        existing.setTaxId(updated.getTaxId());

        return entityRepository.save(existing);
    }

    public void deleteEntities(Long EntitiesId) {
        entityRepository.deleteById(EntitiesId);
    }

    public CustomResponse<List<EntityDTO>> getAllEntitiesWithUserStatus(Long userId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var allEntities = entityRepository.findAll();

            // Map: entityId → latest UserEntity membership row
            var membershipMap = user.getUserEntities().stream()
                    .collect(Collectors.toMap(
                            ue -> ue.getEntity().getEntityId(),
                            ue -> ue,
                            (a, b) -> a.getJoinedAt().isAfter(b.getJoinedAt()) ? a : b
                    ));

            var entityDTOs = allEntities.stream()
                    .map(e -> {
                        var membership = membershipMap.get(e.getEntityId());

                        boolean joined = membership != null && membership.getLeftAt() == null;

                        return EntityDTO.builder()
                                .entityId(e.getEntityId())
                                .name(e.getName())
                                .abbreviation(e.getAbbreviation())
                                .associatedSchool(e.getAssociatedSchool())
                                .entityType(e.getEntityType() != null ? e.getEntityType().getType() : null)

                                .joined(joined)
                                .joinedAt(membership != null ? String.valueOf(membership.getJoinedAt()) : null)
                                .leftAt(membership != null ? String.valueOf(membership.getLeftAt()) : null)

                                .build();
                    })
                    .toList();

            return new CustomResponse<>(
                    "Entities fetched successfully with user join status.",
                    entityDTOs,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Unexpected error while fetching entities: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }



    @Transactional
    public CustomResponse<String> joinEntity(Long userId, Long entityId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var entity = entityRepository.findById(entityId)
                    .orElseThrow(() -> new RuntimeException("Entity not found"));

            // Check if active membership exists
            var existing = userEntityRepository.findActiveMembership(userId, entityId);

            if (existing != null) {
                return new CustomResponse<>(
                        "User already joined this entity.",
                        null,
                        HttpStatus.CONFLICT,
                        "409"
                );
            }

            UserEntity ue = new UserEntity();
            ue.setUser(user);
            ue.setEntity(entity);
            ue.setJoinedAt(Instant.now());
            ue.setLeftAt(null);

            userEntityRepository.save(ue);

            return new CustomResponse<>(
                    "User successfully joined the entity.",
                    null,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error joining entity: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }

    }
    @Transactional
    public CustomResponse<String> leaveEntity(Long userId, Long entityId) {
        try {
            var membership = userEntityRepository.findActiveMembership(userId, entityId);

            if (membership == null) {
                return new CustomResponse<>(
                        "User is not currently a member of this entity.",
                        null,
                        HttpStatus.NOT_FOUND,
                        "404"
                );
            }

            membership.setLeftAt(Instant.now());
            userEntityRepository.save(membership);

            return new CustomResponse<>(
                    "User successfully left the entity.",
                    null,
                    HttpStatus.OK,
                    "200"
            );

        } catch (Exception ex) {
            return new CustomResponse<>(
                    "Error leaving entity: " + ex.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "500"
            );
        }
    }



}

