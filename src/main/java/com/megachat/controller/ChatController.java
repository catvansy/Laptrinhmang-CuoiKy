package com.megachat.controller;

import com.megachat.model.ChatMessage;
import com.megachat.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<?> getConversation(@PathVariable Long friendId,
                                             @RequestParam(required = false) Long afterId,
                                             HttpSession session) {
        try {
            Long userId = getUserId(session);
            List<Map<String, Object>> messages = chatMessageService
                .getConversation(userId, friendId, afterId)
                .stream()
                .map(message -> Map.<String, Object>of(
                    "id", message.getId(),
                    "senderId", message.getSender().getId(),
                    "receiverId", message.getReceiver().getId(),
                    "content", message.getContent(),
                    "createdAt", message.getCreatedAt()
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "messages", messages
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<?> sendMessage(@PathVariable Long friendId,
                                         @RequestBody Map<String, String> body,
                                         HttpSession session) {
        try {
            Long userId = getUserId(session);
            String content = body.get("content");
            ChatMessage message = chatMessageService.sendMessage(userId, friendId, content);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", Map.of(
                    "id", message.getId(),
                    "senderId", message.getSender().getId(),
                    "receiverId", message.getReceiver().getId(),
                    "content", message.getContent(),
                    "createdAt", message.getCreatedAt()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private Long getUserId(HttpSession session) throws Exception {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new Exception("Bạn cần đăng nhập để thực hiện thao tác này");
        }
        return userId;
    }
}

