package com.megachat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * ChatEndpoint - WebSocket Handler cho MegaChat Web
 * Xử lý kết nối, nhắn tin, phát sóng
 */
@Component
public class ChatEndpoint extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Map<WebSocketSession, String> usernames = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        usernames.put(session, "Anonymous");
        logger.info("✓ Client kết nối: " + session.getId());
        
        // Gửi thông báo cho tất cả
        broadcast("[SYSTEM] Người dùng mới vào chat (" + sessions.size() + " người online)");
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // Phân tích tin nhắn: "USER:username" hoặc chat message
            if (payload.startsWith("USER:")) {
                String username = payload.substring(5);
                usernames.put(session, username);
                logger.info("📝 User đặt tên: " + username);
            } else {
                // Phát sóng tin nhắn
                String username = usernames.get(session);
                String fullMessage = "[" + username + "]: " + payload;
                logger.info("💬 " + fullMessage);
                broadcast(fullMessage);
            }
        } catch (Exception e) {
            logger.severe("✗ Lỗi: " + e.getMessage());
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String username = usernames.remove(session);
        sessions.remove(session);
        logger.info("✗ Client ngắt kết nối. Còn: " + sessions.size());
        
        if (username != null) {
            broadcast("[SYSTEM] " + username + " rời khỏi chat (" + sessions.size() + " người online)");
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.severe("✗ Lỗi WebSocket: " + exception.getMessage());
    }
    
    /**
     * Phát sóng tin nhắn cho tất cả client
     */
    private static void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.warning("✗ Lỗi gửi: " + e.getMessage());
                }
            }
        }
    }
}
