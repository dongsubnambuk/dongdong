package com.capstone_ex.message_server.WebSocket.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    @Getter
    private static final ConcurrentHashMap<String, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public MessageWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String userId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("userId");

        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(session);
            System.out.println("새로운 WebSocket 연결: " + session.getId() + " (User ID: " + userId + ")");
            //session.sendMessage(new TextMessage("WebSocket connection established successfully for userId: " + userId));
        } else {
            System.out.println("WebSocket 연결 시 userId가 없음: " + session.getId());
            session.close(CloseStatus.BAD_DATA);
        }
        printAllSessions();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeSession(session);
        System.out.println("WebSocket 연결 종료: " + session.getId());
        printAllSessions();
    }

    private void removeSession(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        List<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public void printAllSessions() {
        System.out.println("현재 연결된 모든 세션:");
        userSessions.forEach((userId, sessions) -> {
            System.out.println("User ID: " + userId);
            sessions.forEach(session ->
                    System.out.println("Session ID: " + session.getId())
            );
        });
    }

    public void sendMessageToAll(String messageContent) {
        TextMessage message = new TextMessage(messageContent);
        userSessions.values().stream()
                .flatMap(List::stream)
                .forEach(session -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void sendMessageToSpecificUser(String userId, String messageContent) {
        List<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageContent));
                        System.out.println("Message sent to session for userId: " + userId + " | Content: " + messageContent);
                    } else {
                        System.out.println("Session closed for userId: " + userId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("No active session found for userId: " + userId);
        }
    }

    public void sendMessageToSpecificUsers(Set<String> userIds, String messageContent) {
        userIds.forEach(userId -> sendMessageToSpecificUser(userId, messageContent));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        removeSession(session);
        System.out.println("WebSocket 오류: " + exception.getMessage());
    }
}
