package com.megachat.controller;

import com.megachat.dto.FriendRequestDto;
import com.megachat.model.Friendship;
import com.megachat.model.User;
import com.megachat.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/requests")
    public ResponseEntity<?> sendRequest(@RequestBody FriendRequestDto dto, HttpSession session) {
        try {
            Long userId = getUserId(session);
            Friendship friendship = friendshipService.sendRequest(userId, dto.getTargetUsername(), dto.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã gửi lời mời kết bạn",
                "requestId", friendship.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> listFriends(HttpSession session) {
        try {
            Long userId = getUserId(session);
            List<com.megachat.service.FriendshipService.UserWithOnlineStatus> friends = 
                friendshipService.getAcceptedFriendsWithOnlineStatus(userId);
            List<Map<String, Object>> friendDtos = friends.stream()
                .map(friendStatus -> {
                    User friend = friendStatus.getUser();
                    Map<String, Object> friendMap = new HashMap<>();
                    friendMap.put("id", friend.getId());
                    friendMap.put("username", friend.getUsername());
                    friendMap.put("email", friend.getEmail());
                    friendMap.put("phone", friend.getPhone());
                    friendMap.put("avatarUrl", friend.getAvatarUrl());
                    friendMap.put("chatTheme", friend.getChatTheme());
                    friendMap.put("online", friendStatus.isOnline());
                    friendMap.put("lastSeen", friend.getLastSeen());
                    
                    // Get friendship info for nickname and blocked status
                    try {
                        Friendship friendship = friendshipService.getFriendship(userId, friend.getId());
                        friendMap.put("nickname", friendship.getNickname());
                        friendMap.put("blocked", friendship.getBlocked());
                    } catch (Exception e) {
                        friendMap.put("nickname", null);
                        friendMap.put("blocked", false);
                    }
                    
                    return friendMap;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "friends", friendDtos
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> listRequests(HttpSession session) {
        try {
            Long userId = getUserId(session);
            List<Map<String, Object>> incoming = friendshipService.getIncomingRequests(userId).stream()
                .map(friendship -> toRequestMap(friendship, "incoming"))
                .collect(Collectors.toList());

            List<Map<String, Object>> outgoing = friendshipService.getOutgoingRequests(userId).stream()
                .map(friendship -> toRequestMap(friendship, "outgoing"))
                .collect(Collectors.toList());

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("incoming", incoming);
            body.put("outgoing", outgoing);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId, HttpSession session) {
        try {
            Long userId = getUserId(session);
            friendshipService.acceptRequest(requestId, userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã chấp nhận lời mời kết bạn"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<?> declineRequest(@PathVariable Long requestId, HttpSession session) {
        try {
            Long userId = getUserId(session);
            friendshipService.declineRequest(requestId, userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã từ chối lời mời kết bạn"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private Map<String, Object> toRequestMap(Friendship friendship, String direction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", friendship.getId());
        map.put("requester", Map.of(
            "id", friendship.getRequester().getId(),
            "username", friendship.getRequester().getUsername()
        ));
        map.put("receiver", Map.of(
            "id", friendship.getReceiver().getId(),
            "username", friendship.getReceiver().getUsername()
        ));
        map.put("status", friendship.getStatus().name());
        map.put("message", friendship.getMessage());
        map.put("created_at", friendship.getCreatedAt());
        map.put("responded_at", friendship.getRespondedAt());
        if (direction != null) {
            map.put("direction", direction);
        }
        return map;
    }

    private Long getUserId(HttpSession session) throws Exception {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new Exception("Bạn cần đăng nhập để thực hiện thao tác này");
        }
        return userId;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword, HttpSession session) {
        try {
            Long userId = getUserId(session);
            
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "users", List.of()
                ));
            }

            List<User> users = friendshipService.searchUsers(keyword.trim(), userId);
            
            List<Map<String, Object>> userDtos = users.stream()
                .map(user -> Map.<String, Object>of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "phone", user.getPhone()
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "users", userDtos
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> unfriend(@PathVariable Long friendId, HttpSession session) {
        try {
            Long userId = getUserId(session);
            friendshipService.unfriend(userId, friendId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa bạn bè thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{friendId}/nickname")
    public ResponseEntity<?> updateNickname(@PathVariable Long friendId, 
                                           @RequestBody Map<String, String> request, 
                                           HttpSession session) {
        try {
            Long userId = getUserId(session);
            String nickname = request.get("nickname");
            Friendship friendship = friendshipService.updateNickname(userId, friendId, nickname);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã cập nhật biệt danh",
                "nickname", friendship.getNickname() != null ? friendship.getNickname() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{friendId}/block")
    public ResponseEntity<?> toggleBlock(@PathVariable Long friendId,
                                        @RequestBody Map<String, Boolean> request,
                                        HttpSession session) {
        try {
            Long userId = getUserId(session);
            Boolean blocked = request.get("blocked");
            if (blocked == null) {
                blocked = true;
            }
            Friendship friendship = friendshipService.toggleBlock(userId, friendId, blocked);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", blocked ? "Đã chặn tin nhắn" : "Đã bỏ chặn tin nhắn",
                "blocked", friendship.getBlocked()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<?> getFriendship(@PathVariable Long friendId, HttpSession session) {
        try {
            Long userId = getUserId(session);
            Friendship friendship = friendshipService.getFriendship(userId, friendId);
            Map<String, Object> friendshipMap = new HashMap<>();
            friendshipMap.put("id", friendship.getId());
            friendshipMap.put("nickname", friendship.getNickname());
            friendshipMap.put("blocked", friendship.getBlocked());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "friendship", friendshipMap
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}


