package com.mldtech.nilapp.api.friend.service;

import com.mldtech.nilapp.api.friend.model.Friend;
import com.mldtech.nilapp.api.friend.repository.FriendRepository;
import com.mldtech.nilapp.api.friend.children.FriendStatus.repository.FriendStatusRepository;
import com.mldtech.nilapp.api.users.dto.FriendDTO;
import com.mldtech.nilapp.api.users.repository.UserRepository;
import com.mldtech.nilapp.helper.CustomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository repo;
    private final UserRepository userRepo;
    private final FriendStatusRepository statusRepo;

    // Helper: find relation regardless of who is caller
    private Friend findRelation(Long userId, Long friendId) {
        Friend relation = repo.findByUserUserIdAndFriendUserId(userId, friendId);
        if (relation != null) return relation;
        return repo.findByUserUserIdAndFriendUserId(friendId, userId);
    }

    // Helper: status IDs
    private static final int STATUS_REQUESTED = 1;
    private static final int STATUS_FRIENDS = 2;
    private static final int STATUS_BLOCKED_BY_USER = 3;
    private static final int STATUS_UNFRIENDED = 4;
    private static final int STATUS_BLOCKED_BY_FRIEND = 5;
    private static final int RECEIVED = 6;

    public List<FriendDTO> getAllFriends() {

        return repo.findAll().stream()
                .map(f -> FriendDTO.builder()
                        .yourId(f.getUserId())
                        .friendId(f.getFriend().getUserId())
                        .friendUsername(f.getFriend().getUsername())
                        .statusId(f.getFriendStatus().getFriendStatusId())
                        .status(f.getFriendStatus().getStatus())
                        .statusDescription(f.getFriendStatus().getDescription())
                        .build())
                .toList();
    }

    // SEND FRIEND REQUEST
    public CustomResponse<?> sendFriendRequest(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            return new CustomResponse<>("You cannot friend yourself", HttpStatus.BAD_REQUEST, "400");
        }

        Friend relation = findRelation(userId, friendId);

        // No record → create new with REQUESTED
        if (relation == null) {
            Friend request = Friend.builder()
                    .user(userRepo.getReferenceById(userId))
                    .friend(userRepo.getReferenceById(friendId))
                    .friendStatus(statusRepo.getReferenceById(STATUS_REQUESTED))
                    .build();
            repo.save(request);
            return new CustomResponse<>("Friend Request Sent", HttpStatus.OK, "200");
        }

        int status = relation.getFriendStatus().getFriendStatusId();
        boolean callerIsUserColumn = relation.getUser().getUserId().equals(userId);
        boolean callerIsFriendColumn = relation.getFriend().getUserId().equals(userId);

        // 🔥 NEW LOGIC: If user RECEIVED a request → auto accept
        if (status == RECEIVED) {
            relation.setFriendStatus(statusRepo.getReferenceById(STATUS_FRIENDS));
            repo.save(relation);
            return new CustomResponse<>("Friend Request Auto-Accepted", HttpStatus.OK, "200");
        }

        // UNFRIENDED → allow new request
        if (status == STATUS_UNFRIENDED) {
            relation.setFriendStatus(statusRepo.getReferenceById(STATUS_REQUESTED));
            repo.save(relation);
            return new CustomResponse<>("New Request Sent #4", HttpStatus.OK, "200");
        }

        // BLOCKED_BY_USER (3)
        if (status == STATUS_BLOCKED_BY_USER) {
            if (callerIsUserColumn) {
                relation.setFriendStatus(statusRepo.getReferenceById(STATUS_REQUESTED));
                repo.save(relation);
                return new CustomResponse<>("Friend Request Sent #3", HttpStatus.OK, "200");
            } else {
                return new CustomResponse<>("Request failed", HttpStatus.BAD_REQUEST, "400");
            }
        }

        // BLOCKED_BY_FRIEND (5)
        if (status == STATUS_BLOCKED_BY_FRIEND) {
            if (callerIsFriendColumn) {
                relation.setFriendStatus(statusRepo.getReferenceById(STATUS_REQUESTED));
                repo.save(relation);
                return new CustomResponse<>("Friend Request Sent #5", HttpStatus.OK, "200");
            } else {
                return new CustomResponse<>("Request failed", HttpStatus.BAD_REQUEST, "400");
            }
        }

        // Already requested
        if (status == STATUS_REQUESTED) {
            return new CustomResponse<>("Friend request already pending", HttpStatus.BAD_REQUEST, "400");
        }

        // Already friends
        if (status == STATUS_FRIENDS) {
            return new CustomResponse<>("You are already friends", HttpStatus.BAD_REQUEST, "400");
        }

        return new CustomResponse<>("Invalid friendship state", HttpStatus.BAD_REQUEST, "400");
    }


    // ACCEPT FRIEND REQUEST
    public CustomResponse<?> acceptFriendRequest(Long userId, Long friendId) {

        // The record must exist with REQUESTED status, regardless of who is user/friend
        Friend relation = findRelation(userId, friendId);

        if (relation == null || relation.getFriendStatus().getFriendStatusId() != STATUS_REQUESTED) {
            return new CustomResponse<>("No pending friend request found", HttpStatus.BAD_REQUEST, "400");
        }

        // Only the receiver should accept:
        // If relation.user sent the request, relation.friend must be the one accepting.
        Long senderId = relation.getUser().getUserId();
        Long receiverId = relation.getFriend().getUserId();

        if (!receiverId.equals(userId)) {
            return new CustomResponse<>("You cannot accept this request", HttpStatus.BAD_REQUEST, "400");
        }

        relation.setFriendStatus(statusRepo.getReferenceById(STATUS_FRIENDS));
        repo.save(relation);

        return new CustomResponse<>("Request Accepted", HttpStatus.OK, "200");
    }

    // UNFRIEND
    public CustomResponse<?> unfriend(Long userId, Long friendId) {

        Friend relation = findRelation(userId, friendId);

        if (relation == null || relation.getFriendStatus().getFriendStatusId() != STATUS_FRIENDS) {
            return new CustomResponse<>("No active friendship exists", HttpStatus.BAD_REQUEST, "400");
        }

        relation.setFriendStatus(statusRepo.getReferenceById(STATUS_UNFRIENDED));
        repo.save(relation);

        return new CustomResponse<>("Unfriend #4", HttpStatus.OK, "200");
    }

    // BLOCK
    public CustomResponse<?> blockUser(Long userId, Long friendId) {

        if (userId.equals(friendId)) {
            return new CustomResponse<>("You cannot block yourself", HttpStatus.BAD_REQUEST, "400");
        }

        Friend relation = findRelation(userId, friendId);

        // No record → create one with user=userId, friend=friendId, blocked_by_user
        if (relation == null) {
            Friend block = Friend.builder()
                    .user(userRepo.getReferenceById(userId))
                    .friend(userRepo.getReferenceById(friendId))
                    .friendStatus(statusRepo.getReferenceById(STATUS_BLOCKED_BY_USER))
                    .build();
            repo.save(block);
            return new CustomResponse<>("Block Random", HttpStatus.OK, "200");
        }

        boolean callerIsUserColumn = relation.getUser().getUserId().equals(userId);
        boolean callerIsFriendColumn = relation.getFriend().getUserId().equals(userId);

        // Caller is user column → they are blocking friend → status 3
        if (callerIsUserColumn) {
            relation.setFriendStatus(statusRepo.getReferenceById(STATUS_BLOCKED_BY_USER));
        }
        // Caller is friend column → they are blocking user → status 5
        else if (callerIsFriendColumn) {
            relation.setFriendStatus(statusRepo.getReferenceById(STATUS_BLOCKED_BY_FRIEND));
        } else {
            return new CustomResponse<>("Invalid block operation", HttpStatus.BAD_REQUEST, "400");
        }

        repo.save(relation);
        return new CustomResponse<>("Block friend", HttpStatus.OK, "200");
    }

    // UNBLOCK (with option to refriend)
    public CustomResponse<?> unblockUser(Long userId, Long friendId, boolean refriend) {

        Friend relation = findRelation(userId, friendId);

        if (relation == null) {
            return new CustomResponse<>("No relationship exists", HttpStatus.BAD_REQUEST, "400");
        }

        int status = relation.getFriendStatus().getFriendStatusId();
        boolean callerIsUserColumn = relation.getUser().getUserId().equals(userId);
        boolean callerIsFriendColumn = relation.getFriend().getUserId().equals(userId);

        // Caller can only unblock if they are the blocker:
        // status 3 → user column blocked friend
        // status 5 → friend column blocked user
        if (status == STATUS_BLOCKED_BY_USER && !callerIsUserColumn) {
            return new CustomResponse<>("You cannot unblock this user", HttpStatus.BAD_REQUEST, "400");
        }
        if (status == STATUS_BLOCKED_BY_FRIEND && !callerIsFriendColumn) {
            return new CustomResponse<>("You cannot unblock this user", HttpStatus.BAD_REQUEST, "400");
        }

        if (refriend) {
            relation.setFriendStatus(statusRepo.getReferenceById(STATUS_FRIENDS));
            repo.save(relation);
            return new CustomResponse<>("Refriend", HttpStatus.OK, "200");
        }

        relation.setFriendStatus(statusRepo.getReferenceById(STATUS_UNFRIENDED));
        repo.save(relation);

        return new CustomResponse<>("User unblocked", HttpStatus.OK, "200");
    }
}
