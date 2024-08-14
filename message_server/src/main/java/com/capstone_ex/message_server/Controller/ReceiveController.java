package com.capstone_ex.message_server.Controller;

import com.capstone_ex.message_server.DTO.ReceiveDTO;
import com.capstone_ex.message_server.Service.Communication.CommunicationService;
import com.capstone_ex.message_server.Service.Receive.ReceiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message/receiver")
public class ReceiveController {
    private final ReceiveService receiveService;
    private final SendController sendController;
    private final CommunicationService communicationService;

    public ReceiveController(ReceiveService receiveService, SendController sendController, CommunicationService communicationService) {
        this.receiveService = receiveService;
        this.sendController = sendController;
        this.communicationService = communicationService;
    }

    @PostMapping
    public ResponseEntity<?> receiveMessage(@RequestBody ReceiveDTO receiveDTO) {
        try {
            // 메시지 처리 및 저장
            receiveService.processReceivedMessage(receiveDTO);

            // 메시지 전송
            sendController.sendMessageToChatRoom(receiveDTO.getChatRoomId(), receiveDTO.getMessageContent());

            return ResponseEntity.ok("성공쓰");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패염");
        }
    }

    @PostMapping("/update-chat-members/{chatRoomId}/{uniqueId}")
    public ResponseEntity<?> updateChatMembers(@PathVariable Long chatRoomId, @PathVariable String uniqueId) {
        try {
            // 사용자 정보를 업데이트하기 위해 CommunicationService 호출
            communicationService.saveUsersToChatRoom(chatRoomId, uniqueId);
            return ResponseEntity.ok("Chat members updated successfully.");
        } catch (Exception e) {
            e.printStackTrace(); // 예외를 출력하여 원인을 파악합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update chat members.");
        }
    }


    @DeleteMapping("/remove-user/{chatRoomId}/{uniqueId}")
    public ResponseEntity<?> deleteUserFromChatRoom(@PathVariable Long chatRoomId, @PathVariable String uniqueId) {
        try {
            receiveService.removeUserFromChatRoom(chatRoomId, uniqueId);
            return ResponseEntity.ok("삭제 성공쓰");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove user.");
        }
    }
}
