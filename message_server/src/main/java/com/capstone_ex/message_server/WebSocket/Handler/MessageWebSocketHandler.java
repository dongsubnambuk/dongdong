package com.capstone_ex.message_server.WebSocket.Handler;

import com.capstone_ex.message_server.DTO.SendDTO;
import com.capstone_ex.message_server.DTO.checkDTO;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.UserRepository;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository; // UserRepository 주입

    @Getter
    private static final Map<String, Map<Long, List<WebSocketSession>>> userSessions = new ConcurrentHashMap<>();

    public MessageWebSocketHandler(ObjectMapper objectMapper, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String userId = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("userId");

        String chatRoomIdStr = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("chatRoomId");

        Long chatRoomId = null;
        try {
            if (chatRoomIdStr != null) {
                chatRoomId = Long.parseLong(chatRoomIdStr);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid chatRoomId: " + chatRoomIdStr);
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (userId != null && chatRoomId != null) {
            userSessions
                    .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(chatRoomId, k -> new CopyOnWriteArrayList<>())
                    .add(session);
            session.getAttributes().put("userId", userId);
            session.getAttributes().put("chatRoomId", chatRoomId);

            System.out.println("새로운 WebSocket 연결: " + session.getId() + " (User ID: " + userId + ", Chat Room ID: " + chatRoomId + ")");
        } else {
            System.out.println("WebSocket 연결 시 userId 또는 chatRoomId가 없음: " + session.getId());
            session.close(CloseStatus.BAD_DATA);
        }
        printAllSessions();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket 연결 종료: " + session.getId());
        removeSession(session);
        printAllSessions();
    }

    private void removeSession(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        Long chatRoomId = (Long) session.getAttributes().get("chatRoomId");

        if (userId != null && chatRoomId != null) {
            Map<Long, List<WebSocketSession>> chatRoomSessions = userSessions.get(userId);
            if (chatRoomSessions != null) {
                List<WebSocketSession> sessions = chatRoomSessions.get(chatRoomId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        chatRoomSessions.remove(chatRoomId);
                    }
                    if (chatRoomSessions.isEmpty()) {
                        userSessions.remove(userId);
                    }
                    System.out.println("세션 제거됨: " + session.getId() + " (User ID: " + userId + ", Chat Room ID: " + chatRoomId + ")");
                }
            }
        } else {
            // sessionId를 사용하여 세션을 제거하는 로직 추가
            for (Map.Entry<String, Map<Long, List<WebSocketSession>>> userEntry : userSessions.entrySet()) {
                Map<Long, List<WebSocketSession>> chatRoomSessions = userEntry.getValue();
                for (Map.Entry<Long, List<WebSocketSession>> chatRoomEntry : chatRoomSessions.entrySet()) {
                    List<WebSocketSession> sessions = chatRoomEntry.getValue();
                    sessions.removeIf(s -> s.getId().equals(session.getId()));
                    if (sessions.isEmpty()) {
                        chatRoomSessions.remove(chatRoomEntry.getKey());
                    }
                }
                if (chatRoomSessions.isEmpty()) {
                    userSessions.remove(userEntry.getKey());
                }
            }
            System.out.println("세션 제거됨 (sessionId로 제거): " + session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("WebSocket 오류: " + exception.getMessage());
        removeSession(session);
    }

    public void printAllSessions() {
        System.out.println("현재 연결된 모든 세션:");
        userSessions.forEach((userId, chatRooms) -> {
            System.out.println("User ID: " + userId);
            chatRooms.forEach((chatRoomId, sessions) -> {
                System.out.println("  Chat Room ID: " + chatRoomId);
                sessions.forEach(session ->
                        System.out.println("    Session ID: " + session.getId())
                );
            });
        });
    }

    public void sendMessageToAll(String messageContent) {
        TextMessage message = new TextMessage(messageContent);
        userSessions.values().stream()
                .flatMap(chatRoomSessions -> chatRoomSessions.values().stream())
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

    public void sendMessageToSpecificUserInChatRoom(String userId, Long chatRoomId, String messageContent) {
        Map<Long, List<WebSocketSession>> chatRoomSessions = userSessions.get(userId);
        if (chatRoomSessions != null) {
            List<WebSocketSession> sessions = chatRoomSessions.get(chatRoomId);
            if (sessions != null) {
                sessions.forEach(session -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(messageContent));
                            System.out.println("Message sent to session for userId: " + userId + " in chatRoomId: " + chatRoomId + " | Content: " + messageContent);
                        } else {
                            System.out.println("Session closed for userId: " + userId + " in chatRoomId: " + chatRoomId);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                // UserEntity 업데이트 로직 추가
                updateUserMessageId(userId, chatRoomId, messageContent);
            } else {
                System.out.println("No active session found for chatRoomId: " + chatRoomId + " for userId: " + userId);
            }
        } else {
            System.out.println("No active session found for userId: " + userId);
        }
    }

    public void sendMessageToSpecificUsersInChatRoom(Set<String> userIds, Long chatRoomId, String messageContent) {
        userIds.forEach(userId -> sendMessageToSpecificUserInChatRoom(userId, chatRoomId, messageContent));
    }

    public void sendReadStatusToChatRoom(Long chatRoomId) {
        List<checkDTO> readStatusList = new ArrayList<>();

        userSessions.forEach((userId, chatRooms) -> {
            if (chatRooms.containsKey(chatRoomId)) {
                chatRooms.get(chatRoomId).forEach(session -> {
                    Long lastMessageId = (Long) session.getAttributes().get("lastMessageId"); // 세션에 저장된 마지막 메시지 ID 가정
                    if (lastMessageId != null) {
                        // UserEntity 업데이트 로직 추가
                        updateUserMessageId(userId, chatRoomId, lastMessageId);

                        checkDTO dto = checkDTO.builder()
                                .messageId(lastMessageId)
                                .chatRoomId(chatRoomId)
                                .build();
                        readStatusList.add(dto);
                    }
                });
            }
        });

        // 채팅방에 포함된 세션이 없는 사용자의 messageId 조회 및 추가
        List<UserEntity> usersInChatRoom = userRepository.findByChatRoomId(chatRoomId);
        for (UserEntity user : usersInChatRoom) {
            if (!userSessions.containsKey(user.getUserId()) || !userSessions.get(user.getUserId()).containsKey(chatRoomId)) {
                checkDTO dto = checkDTO.builder()
                        .messageId(user.getMessageId())
                        .chatRoomId(chatRoomId)
                        .build();
                readStatusList.add(dto);
            }
        }

        // 읽음 상태 리스트를 JSON으로 변환하여 소켓으로 전송
        String messageContent;
        try {
            messageContent = objectMapper.writeValueAsString(readStatusList);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 해당 채팅방에 속한 모든 세션에 메시지 전송
        userSessions.values().stream()
                .flatMap(chatRoomSessions -> chatRoomSessions.getOrDefault(chatRoomId, List.of()).stream())
                .forEach(session -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(messageContent));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void updateUserMessageId(String userId, Long chatRoomId, Long messageId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserIdAndChatRoomId(userId, chatRoomId);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            userEntity.setMessageId(messageId);
            userRepository.save(userEntity);
        }
    }


    private void updateUserMessageId(String userId, Long chatRoomId, String messageContent) {
        try {
            SendDTO sendDTO = objectMapper.readValue(messageContent, SendDTO.class);
            updateUserMessageId(userId, chatRoomId, sendDTO.getMessageId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
