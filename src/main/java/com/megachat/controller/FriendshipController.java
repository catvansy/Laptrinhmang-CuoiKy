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
            List<User> friends = friendshipService.getAcceptedFriends(userId);
            List<Map<String, Object>> friendDtos = friends.stream()
                .map(friend -> Map.<String, Object>of(
                    "id", friend.getId(),
                    "username", friend.getUsername(),
                    "email", friend.getEmail(),
                    "phone", friend.getPhone()
                ))
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
}


