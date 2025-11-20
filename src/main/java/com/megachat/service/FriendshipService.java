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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final com.megachat.service.UserService userService;

    public FriendshipService(FriendshipRepository friendshipRepository, 
                             UserRepository userRepository,
                             com.megachat.service.UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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
    
    /**
     * Lấy danh sách bạn bè với trạng thái online/offline
     */
    public List<UserWithOnlineStatus> getAcceptedFriendsWithOnlineStatus(Long userId) throws Exception {
        User user = getUserOrThrow(userId);
        Set<Long> onlineUserIds = com.megachat.websocket.ChatEndpoint.getOnlineUserIds();
        
        return friendshipRepository.findAcceptedFriendships(user)
            .stream()
            .map(friendship -> {
                User friend = friendship.getRequester().equals(user)
                    ? friendship.getReceiver()
                    : friendship.getRequester();
                boolean isOnline = onlineUserIds.contains(friend.getId());
                return new UserWithOnlineStatus(friend, isOnline);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Inner class để chứa user và trạng thái online
     */
    public static class UserWithOnlineStatus {
        private final User user;
        private final boolean online;
        
        public UserWithOnlineStatus(User user, boolean online) {
            this.user = user;
            this.online = online;
        }
        
        public User getUser() {
            return user;
        }
        
        public boolean isOnline() {
            return online;
        }
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

    /**
     * Tìm kiếm user theo username hoặc email
     */
    public List<User> searchUsers(String keyword, Long excludeUserId) {
        return userService.searchUsers(keyword, excludeUserId);
    }

    /**
     * Xóa bạn bè (unfriend)
     */
    @Transactional
    public void unfriend(Long userId, Long friendId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new Exception("Không tìm thấy người bạn"));

        if (user.getId().equals(friendId)) {
            throw new Exception("Không thể tự xóa chính mình");
        }

        Friendship friendship = friendshipRepository.findAcceptedFriendship(user, friend)
            .orElseThrow(() -> new Exception("Hai người chưa là bạn bè"));

        // Xóa friendship (có thể xóa trực tiếp hoặc set status = DECLINED)
        friendshipRepository.delete(friendship);
    }

    /**
     * Cập nhật biệt danh cho bạn bè
     */
    @Transactional
    public Friendship updateNickname(Long userId, Long friendId, String nickname) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new Exception("Không tìm thấy người bạn"));

        Friendship friendship = friendshipRepository.findAcceptedFriendship(user, friend)
            .orElseThrow(() -> new Exception("Hai người chưa là bạn bè"));

        // Lưu nickname từ phía user (requester hoặc receiver)
        friendship.setNickname(nickname != null && !nickname.trim().isEmpty() ? nickname.trim() : null);
        return friendshipRepository.save(friendship);
    }

    /**
     * Chặn/bỏ chặn tin nhắn từ bạn bè
     */
    @Transactional
    public Friendship toggleBlock(Long userId, Long friendId, boolean blocked) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new Exception("Không tìm thấy người bạn"));

        Friendship friendship = friendshipRepository.findAcceptedFriendship(user, friend)
            .orElseThrow(() -> new Exception("Hai người chưa là bạn bè"));

        friendship.setBlocked(blocked);
        return friendshipRepository.save(friendship);
    }

    /**
     * Lấy friendship giữa hai user
     */
    public Friendship getFriendship(Long userId, Long friendId) throws Exception {
        User user = getUserOrThrow(userId);
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new Exception("Không tìm thấy người bạn"));

        return friendshipRepository.findAcceptedFriendship(user, friend)
            .orElseThrow(() -> new Exception("Hai người chưa là bạn bè"));
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}


