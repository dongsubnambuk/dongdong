package com.capstone_ex.loginserver.WebSocket.handler;

import com.capstone_ex.loginserver.Service.UserService;
import com.capstone_ex.loginserver.Service.CustomUserDetailsService;
import com.capstone_ex.loginserver.Security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserWebSocketHandler extends TextWebSocketHandler {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws IOException {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = payload.get("type");

        if ("register".equals(type)) {
            handleRegister(session, payload);
        } else if ("login".equals(type)) {
            handleLogin(session, payload);
        }
    }

    private void handleRegister(WebSocketSession session, Map<String, String> payload) throws IOException {
        String email = payload.get("email");
        String nickname = payload.get("nickname");
        String password = payload.get("password");

        try {
            userService.registerUser(email, nickname, password);
            session.sendMessage(new org.springframework.web.socket.TextMessage("Registration successful"));
        } catch (IllegalArgumentException e) {
            session.sendMessage(new org.springframework.web.socket.TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleLogin(WebSocketSession session, Map<String, String> payload) throws IOException {
        String email = payload.get("email");
        String password = payload.get("password");

        try {
            // Authentication logic (simplified for demonstration)
            var authenticationToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, password);
            var authentication = customUserDetailsService.loadUserByUsername(email);

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(email);

            session.sendMessage(new org.springframework.web.socket.TextMessage("Login successful: " + token));
        } catch (Exception e) {
            session.sendMessage(new org.springframework.web.socket.TextMessage("Error: " + e.getMessage()));
        }
    }
}