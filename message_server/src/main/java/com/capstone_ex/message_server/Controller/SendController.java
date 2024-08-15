package com.capstone_ex.message_server.Controller;

import com.capstone_ex.message_server.DAO.Send.SendDAO;
import com.capstone_ex.message_server.DTO.SendDTO;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.UserRepository;
import com.capstone_ex.message_server.WebSocket.Controller.MessageWebSocketController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message/sender")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.6:3000"})
public class SendController {
    private final MessageWebSocketController messageWebSocketController;
    private final UserRepository userRepository;
    private final SendDAO sendDAO;


    public SendController(MessageWebSocketController messageWebSocketController, UserRepository userRepository, SendDAO sendDAO) {
        this.messageWebSocketController = messageWebSocketController;
        this.userRepository = userRepository;
        this.sendDAO = sendDAO;
    }

    public void sendMessageToChatRoom(Long chatRoomId, String messageContent) {
        // DB에서 해당 chatRoomId에 속한 모든 userId를 가져옴
        Set<String> userIds = userRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toSet());

        // WebSocket을 통해 해당 userId에 해당하는 모든 세션에 메시지 전송
        messageWebSocketController.sendMessageToSpecificUsers(userIds, messageContent);
    }

    @GetMapping("/{chatRoomId}/read-all")
    public ResponseEntity<?> sendAllMessagesInChatRoom(@PathVariable Long chatRoomId) {
        // SendDAO를 통해 해당 chatRoomId의 모든 메시지를 최신순으로 가져옴
        List<SendDTO> sendDTOList = sendDAO.getMessagesByChatRoomId(chatRoomId);

        // DB에서 해당 chatRoomId에 속한 모든 userId를 가져옴
        Set<String> userIds = sendDAO.getUserIdsByChatRoomId(chatRoomId);

        try {
            // WebSocket을 통해 해당 userId에 해당하는 모든 세션에 메시지 전송
            for (String userId : userIds) {
                for (SendDTO sendDTO : sendDTOList) {
                    try {
                        String messageContent = sendDTO.getMessageContent();
                        messageWebSocketController.sendMessageToSpecificUser(userId, messageContent);
                        System.out.println("Sent message to user " + userId + ": " + messageContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패염");
        }
        return ResponseEntity.ok("성공쓰");
    }
}
