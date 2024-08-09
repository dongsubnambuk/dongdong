package com.capstone_ex.chat_server.Controller;

import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import com.capstone_ex.chat_server.Service.ChatRoom.ChatRoomService;
import com.capstone_ex.chat_server.Service.UserInfo.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserInfoService userInfoService;

    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        ChatRoomDTO createdChatRoom = chatRoomService.createChatRoom(chatRoomDTO.getChatName(), chatRoomDTO.getDescription(), null);
        return ResponseEntity.ok(createdChatRoom);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomById(@PathVariable Long chatRoomId) {
        ChatRoomDTO chatRoomDTO = chatRoomService.getChatRoomById(chatRoomId);
        return ResponseEntity.ok(chatRoomDTO);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms() {
        List<ChatRoomDTO> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatRoomId}/addUser")
    public ResponseEntity<Void> addUserToChatRoom(@PathVariable Long chatRoomId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        UserInfoEntity user = userInfoService.getUserByEmail(email);
        chatRoomService.addUserToChatRoom(user, chatRoomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{chatRoomId}/removeUser")
    public ResponseEntity<Void> removeUserFromChatRoom(@PathVariable Long chatRoomId, @RequestBody Map<String, String> request) {
        String email = request.get("email");
        UserInfoEntity user = userInfoService.getUserByEmail(email);
        chatRoomService.removeUserFromChatRoom(user, chatRoomId);
        return ResponseEntity.ok().build();
    }
}
