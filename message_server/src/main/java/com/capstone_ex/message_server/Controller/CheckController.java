package com.capstone_ex.message_server.Controller;

import com.capstone_ex.message_server.WebSocket.Handler.MessageWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message/checker")
public class CheckController {
    private final MessageWebSocketHandler messageWebSocketHandler;

    public CheckController(MessageWebSocketHandler messageWebSocketHandler) {
        this.messageWebSocketHandler = messageWebSocketHandler;
    }

    @GetMapping
    public ResponseEntity<?> checkConnection(){
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/session-check")
    public ResponseEntity<?> checkSession(){
        messageWebSocketHandler.printAllSessions();
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/{message}")
    public ResponseEntity<?> checkMessage(@PathVariable String message){
        messageWebSocketHandler.sendMessageToAll(message);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/read-status/{chatRoomId}")
    public ResponseEntity<?> sendReadStatusToChatRoom(@PathVariable Long chatRoomId) {
        messageWebSocketHandler.sendReadStatusToChatRoom(chatRoomId);
        return ResponseEntity.ok("Read status sent to chat room " + chatRoomId);
    }
}
