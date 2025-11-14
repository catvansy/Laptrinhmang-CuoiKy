package com.megachat.service;

import com.megachat.model.Friendship;
import com.megachat.model.FriendshipStatus;
import com.megachat.model.User;
import com.megachat.repository.FriendshipRepository;
import com.megachat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public List<User> getAcceptedFriends(Long userId) throws Exception {
        User user = getUserOrThrow(userId);
        return friendshipRepository.findAcceptedFriendships(user)
            .stream()
            .map(friendship -> friendship.getRequester().equals(user)
                ? friendship.getReceiver()
                : friendship.getRequester())
            .collect(Collectors.toList());
    }

    public List<Friendship> getIncomingRequests(Long userId) throws Exception {
        User user = getUserOrThrow(userId);
        return friendshipRepository.findByReceiverAndStatus(user, FriendshipStatus.PENDING);
    }

    public List<Friendship> getOutgoingRequests(Long userId) throws Exception {
        User user = getUserOrThrow(userId);
        return friendshipRepository.findByRequesterAndStatus(user, FriendshipStatus.PENDING);
    }

    @Transactional
    public Friendship sendRequest(Long requesterId, String targetUsername, String message) throws Exception {
        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập username người nhận");
        }

        User requester = getUserOrThrow(requesterId);
        User receiver = userRepository.findByUsername(targetUsername.trim())
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng có username " + targetUsername));

        if (requester.getId().equals(receiver.getId())) {
            throw new Exception("Không thể tự gửi lời mời cho chính mình");
        }

        boolean alreadyExists = friendshipRepository.existsActiveFriendship(
            requester,
            receiver,
            EnumSet.of(FriendshipStatus.PENDING, FriendshipStatus.ACCEPTED)
        );

        if (alreadyExists) {
            throw new Exception("Bạn đã gửi lời mời hoặc đã kết bạn với người này");
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setMessage(message);

        return friendshipRepository.save(friendship);
    }

    @Transactional
    public Friendship acceptRequest(Long requestId, Long receiverId) throws Exception {
        User receiver = getUserOrThrow(receiverId);
        Friendship friendship = friendshipRepository.findByIdAndReceiver(requestId, receiver)
            .orElseThrow(() -> new Exception("Không tìm thấy lời mời kết bạn"));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new Exception("Lời mời đã được xử lý trước đó");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setRespondedAt(LocalDateTime.now());
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public Friendship declineRequest(Long requestId, Long receiverId) throws Exception {
        User receiver = getUserOrThrow(receiverId);
        Friendship friendship = friendshipRepository.findByIdAndReceiver(requestId, receiver)
            .orElseThrow(() -> new Exception("Không tìm thấy lời mời kết bạn"));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new Exception("Lời mời đã được xử lý trước đó");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        friendship.setRespondedAt(LocalDateTime.now());
        return friendshipRepository.save(friendship);
    }

    private User getUserOrThrow(Long userId) throws Exception {
        if (userId == null) {
            throw new Exception("Bạn chưa đăng nhập");
        }
        return userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng"));
    }
}


