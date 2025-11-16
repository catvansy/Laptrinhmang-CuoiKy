package com.megachat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megachat.model.ChatMessage;
import com.megachat.model.User;
import com.megachat.repository.ChatMessageRepository;
import com.megachat.repository.FriendshipRepository;
import com.megachat.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * ChatEndpoint - WebSocket Handler cho MegaChat Web
 * Xử lý kết nối, nhắn tin real-time theo mô hình Multi Client-Server
 * Hỗ trợ gửi tin nhắn đến đúng người nhận (không broadcast)
 */
@Component
public class ChatEndpoint extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    
    // Quản lý sessions: Map userId -> Set<WebSocketSession> (một user có thể có nhiều tab/device)
    private static final Map<Long, Set<WebSocketSession>> userSessions = Collections.synchronizedMap(new HashMap<>());
    // Map session -> userId để tra cứu nhanh
    private static final Map<WebSocketSession, Long> sessionToUserId = Collections.synchronizedMap(new HashMap<>());
    // Lưu danh sách userId đang online
    private static final Set<Long> onlineUserIds = Collections.synchronizedSet(new HashSet<>());
    
    private final UserRepository userRepository;
    private final ChatMessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ChatEndpoint(UserRepository userRepository,
                       ChatMessageRepository messageRepository,
                       FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.friendshipRepository = friendshipRepository;
    }
    
    /**
     * Kiểm tra user có online không
     */
    public static boolean isUserOnline(Long userId) {
        return onlineUserIds.contains(userId);
    }
    
    /**
     * Lấy danh sách userId đang online
     */
    public static Set<Long> getOnlineUserIds() {
        return new HashSet<>(onlineUserIds);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("✓ WebSocket kết nối: " + session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // Phân tích tin nhắn JSON hoặc plain text
            if (payload.startsWith("{")) {
                // JSON message - tin nhắn chat
                handleJsonMessage(session, payload);
            } else if (payload.startsWith("USER:")) {
                // Đăng ký userId cho session
                handleUserRegistration(session, payload);
            } else {
                logger.warning("⚠ Không nhận dạng được format: " + payload);
            }
        } catch (Exception e) {
            logger.severe("✗ Lỗi xử lý tin nhắn: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Lỗi xử lý tin nhắn: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý đăng ký userId cho session
     */
    private void handleUserRegistration(WebSocketSession session, String payload) {
        try {
            String value = payload.substring(5).trim();
            Long userId = Long.parseLong(value);
            
            // Lưu mapping
            sessionToUserId.put(session, userId);
            userSessions.computeIfAbsent(userId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
            onlineUserIds.add(userId);
            
            logger.info("📝 User " + userId + " đã đăng ký WebSocket (session: " + session.getId() + ")");
            
            // Gửi xác nhận
            sendMessage(session, Map.of(
                "type", "connected",
                "userId", userId,
                "message", "Kết nối WebSocket thành công"
            ));
        } catch (NumberFormatException e) {
            logger.warning("⚠ Invalid userId format: " + payload);
            sendError(session, "UserId không hợp lệ");
        }
    }
    
    /**
     * Xử lý tin nhắn JSON
     */
    private void handleJsonMessage(WebSocketSession session, String payload) throws Exception {
        Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
        String type = (String) messageData.get("type");
        
        if ("message".equals(type)) {
            handleChatMessage(session, messageData);
        } else {
            logger.warning("⚠ Unknown message type: " + type);
        }
    }
    
    /**
     * Xử lý tin nhắn chat
     */
    private void handleChatMessage(WebSocketSession session, Map<String, Object> messageData) throws Exception {
        Long senderId = sessionToUserId.get(session);
        if (senderId == null) {
            sendError(session, "Bạn cần đăng ký userId trước khi gửi tin nhắn");
            return;
        }
        
        Long receiverId = getLongValue(messageData.get("receiverId"));
        String content = (String) messageData.get("content");
        
        if (receiverId == null || content == null || content.trim().isEmpty()) {
            sendError(session, "Thiếu thông tin receiverId hoặc content");
            return;
        }
        
        // Kiểm tra và lưu tin nhắn
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new Exception("Không tìm thấy người gửi"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new Exception("Không tìm thấy người nhận"));
        
        // Kiểm tra friendship
        if (!friendshipRepository.existsAcceptedFriendship(sender, receiver)) {
            sendError(session, "Bạn chỉ có thể nhắn tin với người đã là bạn bè");
            return;
        }
        
        // Lưu tin nhắn vào database
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setContent(content.trim());
        chatMessage = messageRepository.save(chatMessage);
        
        // Tạo response message
        Map<String, Object> responseMessage = Map.of(
            "type", "message",
            "id", chatMessage.getId(),
            "senderId", senderId,
            "receiverId", receiverId,
            "content", chatMessage.getContent(),
            "createdAt", chatMessage.getCreatedAt().toString()
        );
        
        // Gửi tin nhắn cho người gửi (xác nhận)
        sendMessage(session, responseMessage);
        
        // Gửi tin nhắn cho người nhận (nếu đang online)
        sendToUser(receiverId, responseMessage);
        
        logger.info("💬 Tin nhắn từ " + senderId + " đến " + receiverId + ": " + content.substring(0, Math.min(50, content.length())));
    }
    
    /**
     * Gửi tin nhắn đến một user cụ thể (tất cả sessions của user đó)
     */
    private void sendToUser(Long userId, Map<String, Object> message) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null && !sessions.isEmpty()) {
            sessions.removeIf(s -> {
                if (!s.isOpen()) {
                    return true; // Remove closed sessions
                }
                try {
                    sendMessage(s, message);
                    return false;
                } catch (Exception e) {
                    logger.warning("✗ Lỗi gửi tin nhắn đến session: " + e.getMessage());
                    return true; // Remove failed sessions
                }
            });
            
            // Cleanup nếu không còn session nào
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }
    
    /**
     * Gửi tin nhắn đến một session
     */
    private void sendMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.warning("✗ Lỗi gửi tin nhắn: " + e.getMessage());
        }
    }
    
    /**
     * Gửi lỗi đến session
     */
    private void sendError(WebSocketSession session, String errorMessage) {
        sendMessage(session, Map.of(
            "type", "error",
            "message", errorMessage
        ));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        Long userId = sessionToUserId.remove(session);
        
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                    onlineUserIds.remove(userId);
                    logger.info("✗ User " + userId + " offline (không còn session nào)");
                } else {
                    logger.info("✗ User " + userId + " đóng một session (còn " + sessions.size() + " session)");
                }
            }
        }
        
        logger.info("✗ WebSocket ngắt kết nối: " + session.getId() + " (còn " + userSessions.size() + " users online)");
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.severe("✗ Lỗi WebSocket transport: " + exception.getMessage());
        exception.printStackTrace();
    }
    
    /**
     * Helper: Convert Object to Long
     */
    private Long getLongValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
