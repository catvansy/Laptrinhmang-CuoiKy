package com.megachat.config;

import com.megachat.websocket.ChatEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket Configuration - Cấu hình WebSocket cho Spring Boot
 * Hỗ trợ Multi Client-Server với CORS
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final ChatEndpoint chatEndpoint;
    
    public WebSocketConfig(ChatEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatEndpoint, "/ws/chat")
                .setAllowedOrigins("*");  // Allow all origins (có thể hạn chế trong production)
    }
}
