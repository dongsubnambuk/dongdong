package com.capstone_ex.message_server.Service.Receive;

import com.capstone_ex.message_server.DTO.MessageDTO;
import com.capstone_ex.message_server.DTO.ReceiveDTO;

import java.util.Set;

public interface ReceiveService {
    MessageDTO processReceivedMessage(ReceiveDTO receiveDTO);

    Set<String> getUserIdsInChatRoom(Long chatRoomId);
    void removeUserFromChatRoom(Long chatRoomId, String uniqueId);
}
