package com.capstone_ex.message_server.WebSocket.Controller;

import com.capstone_ex.message_server.WebSocket.Handler.MessageWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessageWebSocketHandler messageWebSocketHandler;


    public void sendMessageToAll(String messageContent) {
        Set<WebSocketSession> sessions = messageWebSocketHandler.getSessions();
        TextMessage message = new TextMessage(messageContent);

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 필요한 경우 예외 처리 로직 추가
            }
        }
    }

    public void sendMessageToSession(String sessionId, String messageContent) {
        Set<WebSocketSession> sessions = messageWebSocketHandler.getSessions();
        TextMessage message = new TextMessage(messageContent);

        for (WebSocketSession session : sessions) {
            if (session.getId().equals(sessionId) && session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 필요한 경우 예외 처리 로직 추가
                }
            }
        }
    }

    public void sendMessageToSpecificUser(String userId, String messageContent) throws IOException {
        Set<WebSocketSession> sessions = messageWebSocketHandler.getSessions();
        TextMessage message = new TextMessage(messageContent);

        for (WebSocketSession session : sessions) {
            String sessionUserId = (String) session.getAttributes().get("userId");
            if (sessionUserId != null && sessionUserId.equals(userId) && session.isOpen()) {
                session.sendMessage(message);
            }
        }
    }

    public void sendMessageToSpecificUsers(Set<String> userIds, String messageContent) {
        for (String userId : userIds) {
            try {
                sendMessageToSpecificUser(userId, messageContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}