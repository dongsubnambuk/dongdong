package com.capstone_ex.chat_server.Controller;

import com.capstone_ex.chat_server.DAO.ChatRoom.ChatRoomDAO;
import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.DTO.UserInfoDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Service.ChatRoom.ChatRoomService;
import com.capstone_ex.chat_server.Service.Communication.CommunicationService;
import com.capstone_ex.chat_server.Service.UserInfo.UserInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.6:3000"})
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserInfoService userInfoService;
    private final CommunicationService communicationService;
    private final ChatRoomDAO chatRoomDAO;

    @PostMapping("/create-chat")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        List<ExternalUserInfoDTO> externalUserInfoDTOList = new ArrayList<>();
        externalUserInfoDTOList.add(communicationService.getUserInfo(chatRoomDTO.getCreatorId()));
        externalUserInfoDTOList.add(communicationService.getUserInfo(chatRoomDTO.getSelectedId()));

        for (ExternalUserInfoDTO externalUserInfoDTO : externalUserInfoDTOList) {
            UserInfoEntity user = userInfoService.getUserByUniqueId(externalUserInfoDTO.getUniqueId());

            if (user == null) {
                userInfoService.saveNewUser(externalUserInfoDTO);
            }
        }

        try {
            ChatRoomEntity createdChatRoom = chatRoomService.createChatRoom(chatRoomDTO.getChatName(), externalUserInfoDTOList.get(0).getUniqueId());

            chatRoomService.addUserToChatRoom(externalUserInfoDTOList.get(1).getUniqueId(), createdChatRoom.getId());

            // Message Server 채팅방 업데이트 요청: creatorId
            communicationService.callUpdateChatInfo(createdChatRoom.getId(), externalUserInfoDTOList.get(0).getUniqueId());

            // Message Server 채팅방 업데이트 요청: selectedId
            communicationService.callUpdateChatInfo(createdChatRoom.getId(), externalUserInfoDTOList.get(1).getUniqueId());

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

    @GetMapping("/all-chat")
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms() {
        List<ChatRoomDTO> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatRoomId}/add-user/{uniqueId}")
    public ResponseEntity<?> addUserToChatRoom(@PathVariable Long chatRoomId, @PathVariable String uniqueId) {
        try {
            ExternalUserInfoDTO externalUserInfoDTO = communicationService.getUserInfo(uniqueId);
            UserInfoEntity user = userInfoService.getUserByUniqueId(externalUserInfoDTO.getUniqueId());

            // 유저가 존재하지 않으면 새로 저장
            if (user == null) {
                userInfoService.saveNewUser(externalUserInfoDTO);
                user = userInfoService.getUserByUniqueId(externalUserInfoDTO.getUniqueId()); // 새로 저장한 유저 불러오기
            }

            // 채팅방에 이미 해당 유저가 있는지 확인
            ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(chatRoomId);
            if (chatRoom.getUsers().contains(user)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Already exist user in chatroom");
            }

            chatRoomService.addUserToChatRoom(user.getUniqueId(), chatRoomId);

            // Message Server 채팅방 업데이트 요청
            communicationService.callUpdateChatInfo(chatRoomId, uniqueId);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace(); // 예외 발생시 스택 트레이스를 출력하여 원인 확인
            return ResponseEntity.ok(false);
        }
    }


    @PostMapping("/{chatRoomId}/remove-user/{uniqueId}")
    public ResponseEntity<?> removeUserFromChatRoom(@PathVariable Long chatRoomId, @PathVariable String uniqueId) {
        try {
            ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(chatRoomId);
            UserInfoEntity user = userInfoService.getUserByUniqueId(uniqueId);

            if (chatRoom != null && user != null) {
                // 채팅방에서 유저를 제거
                chatRoom.getUsers().remove(user);

                chatRoomService.saveChatRoom(chatRoom); // 변경된 채팅방 정보를 저장
                // 삭제한 유저 업데이트
                communicationService.callDeleteChatUser(chatRoomId, uniqueId);
                return ResponseEntity.ok(true);
            } else {
                throw new IllegalArgumentException("ChatRoom or User not found");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{chatRoomId}/users")
    public ResponseEntity<List<UserInfoDTO>> getUsersInChatRoom(@PathVariable Long chatRoomId) {
        try {
            List<UserInfoDTO> users = chatRoomService.getUsersInChatRoom(chatRoomId);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{chatRoomId}/user-count")
    public ResponseEntity<Long> getUserCountInChatRoom(@PathVariable Long chatRoomId) {
        try {
            long userCount = chatRoomService.getUserCountInChatRoom(chatRoomId);
            return ResponseEntity.ok(userCount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0L);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<String> apiRoot() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/{uniqueId}/chat-rooms")
    public ResponseEntity<List<ChatRoomDTO>> getChatRoomsForUser(@PathVariable String uniqueId) {
        try {
            List<ChatRoomDTO> chatRooms = chatRoomService.getChatRoomsForUser(uniqueId);
            return ResponseEntity.ok(chatRooms);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }
    }
}
