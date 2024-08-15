package com.capstone_ex.message_server.WebSocket.Controller;

import com.capstone_ex.message_server.WebSocket.Handler.MessageWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final MessageWebSocketHandler messageWebSocketHandler;

    public void sendMessageToAll(String messageContent) {
        messageWebSocketHandler.sendMessageToAll(messageContent);
    }

    public void sendMessageToSpecificUser(String userId, String messageContent) {
        messageWebSocketHandler.sendMessageToSpecificUser(userId, messageContent);
    }

    public void sendMessageToSpecificUsers(Set<String> userIds, String messageContent) {
        messageWebSocketHandler.sendMessageToSpecificUsers(userIds, messageContent);
    }
}
