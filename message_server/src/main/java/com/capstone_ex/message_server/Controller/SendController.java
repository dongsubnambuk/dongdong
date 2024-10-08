package com.capstone_ex.message_server.Controller;

import com.capstone_ex.message_server.DAO.Send.SendDAO;
import com.capstone_ex.message_server.DTO.SendDTO;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.UserRepository;
import com.capstone_ex.message_server.WebSocket.Controller.MessageWebSocketController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message/sender")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.6:3000"})
public class SendController {
    private final MessageWebSocketController messageWebSocketController;
    private final CheckController checkController;
    private final UserRepository userRepository;
    private final SendDAO sendDAO;
    private final ObjectMapper objectMapper;


    public SendController(MessageWebSocketController messageWebSocketController, CheckController checkController, UserRepository userRepository, SendDAO sendDAO, ObjectMapper objectMapper) {
        this.messageWebSocketController = messageWebSocketController;
        this.checkController = checkController;
        this.userRepository = userRepository;
        this.sendDAO = sendDAO;
        this.objectMapper = objectMapper;
    }

    public void sendMessageToChatRoom(Long chatRoomId, SendDTO sendDTO) {
        // DB에서 해당 chatRoomId에 속한 모든 userId를 가져옴
        Set<String> userIds = userRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toSet());

        try {
            // SendDTO 객체를 JSON으로 변환
            String messageContent = objectMapper.writeValueAsString(sendDTO);

            // WebSocket을 통해 해당 userId에 해당하는 모든 세션에 메시지 전송
            messageWebSocketController.sendMessageToSpecificUsersInChatRoom(userIds, chatRoomId, messageContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // CheckController를 통해 읽음 수 전송
        checkController.sendReadStatusToChatRoom(chatRoomId);
    }

    @GetMapping("/{chatRoomId}/read-all")
    public ResponseEntity<?> sendAllMessagesInChatRoom(@PathVariable Long chatRoomId) {
        // SendDAO를 통해 해당 chatRoomId의 모든 메시지를 최신순의 역순으로 가져옴
        List<SendDTO> sendDTOList = sendDAO.getMessagesByChatRoomId(chatRoomId);

        // DB에서 해당 chatRoomId에 속한 모든 userId를 가져옴
        Set<String> userIds = sendDAO.getUserIdsByChatRoomId(chatRoomId);

        try {
            // WebSocket을 통해 해당 userId에 해당하는 모든 세션에 메시지 전송
            for (String userId : userIds) {
                for (SendDTO sendDTO : sendDTOList) {
                    try {
                        // SendDTO 객체를 JSON으로 변환
                        String messageContent = objectMapper.writeValueAsString(sendDTO);
                        messageWebSocketController.sendMessageToSpecificUserInChatRoom(userId, chatRoomId,messageContent);
                        System.out.println("Sent message to user " + userId + ": " + messageContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패염");
        }
        checkController.sendReadStatusToChatRoom(chatRoomId);
        return ResponseEntity.ok("성공쓰");
    }
}
