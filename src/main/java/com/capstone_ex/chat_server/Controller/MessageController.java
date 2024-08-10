package com.capstone_ex.chat_server.Controller;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;
import com.capstone_ex.chat_server.Service.Message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageEntity> sendMessage(@RequestBody MessageEntity messageEntity) {
        MessageEntity sentMessage = messageService.sendMessage(messageEntity);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<MessageEntity>> getMessagesByChatRoomId(@PathVariable Long chatRoomId) {
        List<MessageEntity> messages = messageService.getMessagesByChatRoomId(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}
