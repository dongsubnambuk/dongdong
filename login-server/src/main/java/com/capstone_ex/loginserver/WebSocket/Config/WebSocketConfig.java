package com.capstone_ex.loginserver.WebSocket.Config;

import com.capstone_ex.loginserver.Service.UserService;
import com.capstone_ex.loginserver.Service.CustomUserDetailsService;
import com.capstone_ex.loginserver.Security.JwtTokenUtil;
import com.capstone_ex.loginserver.WebSocket.handler.UserWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    public WebSocketConfig(UserService userService, CustomUserDetailsService customUserDetailsService, JwtTokenUtil jwtTokenUtil, ObjectMapper objectMapper) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new UserWebSocketHandler(userService, customUserDetailsService, jwtTokenUtil, objectMapper), "/ws/user")
                .setAllowedOrigins("http://192.168.0.6:3000", "http://localhost:3000"); // 허용할 프론트엔드 URL
    }
}
