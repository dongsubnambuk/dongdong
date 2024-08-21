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

    public void sendMessageToSpecificUserInChatRoom(String userId, Long chatRoomId, String messageContent) {
        messageWebSocketHandler.sendMessageToSpecificUserInChatRoom(userId, chatRoomId, messageContent);
    }

    public void sendMessageToSpecificUsersInChatRoom(Set<String> userIds, Long chatRoomId, String messageContent) {
        messageWebSocketHandler.sendMessageToSpecificUsersInChatRoom(userIds, chatRoomId, messageContent);
    }
}
