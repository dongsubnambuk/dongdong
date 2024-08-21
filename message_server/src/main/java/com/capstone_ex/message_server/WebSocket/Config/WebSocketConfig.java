package com.capstone_ex.message_server.WebSocket.Config;

import com.capstone_ex.message_server.WebSocket.Handler.MessageWebSocketHandler;
import com.capstone_ex.message_server.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public WebSocketConfig(ObjectMapper objectMapper, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MessageWebSocketHandler(objectMapper, userRepository), "/ws/message")
                .setAllowedOrigins("http://192.168.0.6:3000", "http://localhost:3000"); // 허용할 프론트엔드 URL
    }
}
