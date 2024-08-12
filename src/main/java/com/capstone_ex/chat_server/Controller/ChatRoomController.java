package com.capstone_ex.chat_server.Controller;

import com.capstone_ex.chat_server.DAO.ChatRoom.ChatRoomDAO;
import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Service.ChatRoom.ChatRoomService;
import com.capstone_ex.chat_server.Service.Communication.CommunicationService;
import com.capstone_ex.chat_server.Service.UserInfo.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserInfoService userInfoService;
    private final CommunicationService communicationService;
    private final ChatRoomDAO chatRoomDAO;

    @PostMapping
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        List<ExternalUserInfoDTO> externalUserInfoDTOList = new ArrayList<>();
        externalUserInfoDTOList.add(communicationService.getUserInfo(chatRoomDTO.getCreatorId()));
        externalUserInfoDTOList.add(communicationService.getUserInfo(chatRoomDTO.getSelectedId()));

        for (ExternalUserInfoDTO externalUserInfoDTO : externalUserInfoDTOList) {
            // 유저가 이미 존재하는지 확인
            UserInfoEntity user = userInfoService.getUserByUniqueId(externalUserInfoDTO.getUniqueId());

            // 유저가 존재하지 않으면 새로 저장
            if (user == null) {
                userInfoService.saveNewUser(externalUserInfoDTO);
            }
        }

        try {
            // 채팅방 생성
            ChatRoomEntity createdChatRoom = chatRoomService.createChatRoom(chatRoomDTO.getChatName(), externalUserInfoDTOList.get(0).getUniqueId());

            // 선택된 유저도 채팅방에 추가
            chatRoomService.addUserToChatRoom(externalUserInfoDTOList.get(1).getUniqueId(), createdChatRoom.getId());

            return ResponseEntity.ok(createdChatRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomById(@PathVariable Long chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(chatRoomId);
        return ResponseEntity.ok(new ChatRoomDTO(chatRoom));
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
    public ResponseEntity<?> addUserToChatRoom(@PathVariable Long chatRoomId, @RequestBody String userId) {
        try {
            chatRoomService.addUserToChatRoom(userId, chatRoomId);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/{chatRoomId}/removeUser")
    public ResponseEntity<?> removeUserFromChatRoom(@PathVariable Long chatRoomId, @RequestBody String userId) {
        try {
            chatRoomService.removeUserFromChatRoom(userId, chatRoomId);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }
}
